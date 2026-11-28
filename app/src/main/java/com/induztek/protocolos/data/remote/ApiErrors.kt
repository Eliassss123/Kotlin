package com.induztek.protocolos.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

/**
 * Cuerpo JSON uniforme con el que responden los errores todos los microservicios
 * (record `ApiError` de common-security en el backend).
 *
 * Ejemplo:
 * ```json
 * {"timestamp":"2026-11-23T21:30:00Z","status":404,"error":"Not Found",
 *  "code":"NOT_FOUND","message":"Account not found","path":"/api/usuarios/me","details":[]}
 * ```
 * `details` solo trae datos en errores de validación (400 `VALIDATION_ERROR`).
 */
data class ApiErrorDto(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null,
    val path: String? = null,
    val details: List<FieldDetailDto>? = null
)

data class FieldDetailDto(val field: String? = null, val message: String? = null)

/**
 * Catálogo de códigos HTTP que devuelve el backend Induztek y cómo los trata la app.
 *
 * | HTTP | `code` backend            | Significado / Cuándo ocurre                                                     |
 * |------|---------------------------|---------------------------------------------------------------------------------|
 * | 200  | —                         | OK. Consulta o actualización correcta; el cuerpo trae el recurso.               |
 * | 201  | —                         | Created. Recurso creado (registro, equipo, visita, protocolo, prueba, acta).    |
 * | 202  | —                         | Accepted. Solicitud aceptada; p. ej. código de recuperación enviado al correo.  |
 * | 204  | —                         | No Content. Operación correcta sin cuerpo (verificar código, eliminar).         |
 * | 400  | VALIDATION_ERROR, MALFORMED_REQUEST, INVALID_PARAMETER, MISSING_PARAMETER, MISSING_PART, BAD_REQUEST | Datos inválidos o regla de negocio incumplida (p. ej. código de recuperación inválido o vencido). |
 * | 401  | UNAUTHORIZED / sin cuerpo | Credenciales incorrectas, o token JWT ausente/vencido.                          |
 * | 403  | FORBIDDEN                 | El rol no tiene permiso, o la cuenta está pendiente o desactivada.               |
 * | 404  | NOT_FOUND                 | El recurso no existe (o pertenece a otra empresa para un CLIENT).               |
 * | 405  | METHOD_NOT_ALLOWED        | Método HTTP no soportado en esa ruta (error de programación de la app).         |
 * | 406  | NOT_ACCEPTABLE            | Formato de respuesta no disponible (error de programación de la app).           |
 * | 409  | CONFLICT                  | Duplicado: correo, código de equipo, protocolo ya formalizado, acta repetida.   |
 * | 413  | PAYLOAD_TOO_LARGE         | La imagen supera 10 MB.                                                         |
 * | 415  | UNSUPPORTED_MEDIA_TYPE    | La imagen no es JPEG/PNG.                                                       |
 * | 429  | TOO_MANY_REQUESTS         | 5 intentos de login fallidos: bloqueo de 15 minutos.                            |
 * | 500  | INTERNAL_ERROR            | Error inesperado del servidor.                                                  |
 * | 502  | BAD_GATEWAY               | Un servicio externo falló (p. ej. SMTP al enviar el código), o el gateway/túnel no llegó al servicio. |
 * | 503  | SERVICE_UNAVAILABLE       | Un microservicio del que depende la operación no responde.                      |
 * | 504  | GATEWAY_TIMEOUT           | Un servicio tardó demasiado (p. ej. el correo de recuperación).                 |
 *
 * Errores sin respuesta HTTP: [SocketTimeoutException] (la app esperó más de los timeouts de
 * `RetrofitClient`) e [IOException] (sin conexión, túnel caído, DNS).
 */
object ApiErrors {
    private val gson = Gson()

    /**
     * Lee el cuerpo `ApiError` de una respuesta de error; `null` si no hay cuerpo o no es JSON.
     * OkHttp permite leer el cuerpo una sola vez: llamar a esta función (o a [toUserMessage]) solo una vez por excepción.
     */
    fun parse(exception: HttpException): ApiErrorDto? = try {
        exception.response()?.errorBody()?.string()
            ?.takeIf { it.isNotBlank() }
            ?.let { gson.fromJson(it, ApiErrorDto::class.java) }
    } catch (_: JsonSyntaxException) {
        null
    } catch (_: IOException) {
        null
    } catch (_: IllegalStateException) {
        null
    }

    /** Mensaje genérico en español para cada código HTTP de error. */
    fun messageForStatus(status: Int, apiError: ApiErrorDto? = null): String = when (status) {
        400 -> when (apiError?.code) {
            "VALIDATION_ERROR" -> validationMessage(apiError)
            else -> if (apiError?.message?.contains("recovery code", ignoreCase = true) == true) {
                "El código es inválido o ya expiró. Solicita uno nuevo."
            } else {
                "Los datos enviados no son válidos"
            }
        }
        401 -> "Correo o contraseña incorrectos, o tu sesión expiró"
        403 -> "No tienes permiso, o la cuenta está pendiente de aprobación o desactivada"
        404 -> "No se encontró el recurso solicitado"
        405, 406 -> "La app hizo una solicitud no soportada por el servidor ($status)"
        409 -> "El registro ya existe o entra en conflicto con otro"
        413 -> "La imagen supera el tamaño máximo de 10 MB"
        415 -> "Formato de archivo no soportado. Usa JPEG o PNG"
        429 -> "Demasiados intentos fallidos. Espera 15 minutos e inténtalo de nuevo"
        500 -> "Error interno del servidor. Inténtalo más tarde"
        502 -> if (apiError?.path?.contains("password/recovery") == true) {
            "No se pudo enviar el correo con el código. Inténtalo de nuevo en unos minutos"
        } else {
            "El servidor no pudo completar la operación con un servicio externo (502)"
        }
        503 -> "El servicio no está disponible en este momento. Inténtalo más tarde"
        504 -> if (apiError?.path?.contains("password/recovery") == true) {
            "El envío del correo tardó demasiado. Inténtalo de nuevo"
        } else {
            "El servidor tardó demasiado en responder. Inténtalo de nuevo"
        }
        in 400..499 -> "La solicitud no es válida ($status)"
        else -> "Error del servidor ($status)"
    }

    private fun validationMessage(apiError: ApiErrorDto): String {
        val first = apiError.details?.firstOrNull { !it.field.isNullOrBlank() }
        return if (first != null) "Dato inválido en '${first.field}': ${first.message}" else "Los datos enviados no son válidos"
    }
}

/**
 * Convierte cualquier error en el mensaje que ve el usuario, siempre con su código entre corchetes:
 * - [AppException] → su [CodigoError] (p. ej. `[MED-002] La imagen supera…`).
 * - [HttpException] → `[HTTP-<status>]` + mensaje de [ApiErrors.messageForStatus].
 * - [SocketTimeoutException] → `[RED-001]`; otra [IOException] → `[RED-002]`; cualquier otra → `[APP-001]`.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is AppException -> message ?: error.formatear()
    is HttpException -> "[HTTP-${code()}] " + ApiErrors.messageForStatus(code(), ApiErrors.parse(this))
    is SocketTimeoutException -> CodigoError.RED_001.formatear()
    is IOException -> CodigoError.RED_002.formatear()
    else -> CodigoError.APP_001.formatear()
}
