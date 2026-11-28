package com.induztek.protocolos

import com.induztek.protocolos.data.remote.ApiErrors
import com.induztek.protocolos.data.remote.toUserMessage
import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.httpError
import java.io.IOException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Traducción de errores del backend y de red a mensajes con código (ApiErrors / toUserMessage).
 *
 * Criterios de aceptación:
 * - CA-API-01: se lee el cuerpo ApiError del backend; si no hay JSON se devuelve null.
 * - CA-API-02: cada código HTTP documentado (400-504) tiene un mensaje propio en español.
 * - CA-API-03: el mensaje mostrado de un error HTTP empieza con [HTTP-<status>].
 * - CA-API-04: la recuperación de contraseña explica los fallos de correo [HTTP-502, HTTP-504] y códigos vencidos [HTTP-400].
 * - CA-API-05: timeout [RED-001], sin conexión [RED-002], error de la app con código propio y error inesperado [APP-001].
 */
class ApiErrorsTest {

    // CA-API-01: se leen status, code, message y path del cuerpo de error.
    @Test
    fun parseLeeElCuerpoApiErrorDelBackend() {
        val error = ApiErrors.parse(httpError(404, "NOT_FOUND", "Account not found", "/api/usuarios/me"))!!
        assertEquals(404, error.status)
        assertEquals("NOT_FOUND", error.code)
        assertEquals("Account not found", error.message)
        assertEquals("/api/usuarios/me", error.path)
    }

    // CA-API-01: un 401 sin cuerpo (Spring Security) no rompe el parseo.
    @Test
    fun parseDevuelveNullSiNoHayCuerpoJson() {
        assertNull(ApiErrors.parse(httpError(401)))
    }

    // CA-API-02: todos los códigos documentados tienen mensaje distinto (405 y 406 lo comparten).
    @Test
    fun cadaCodigoDeErrorDocumentadoTieneMensajePropio() {
        val codes = listOf(400, 401, 403, 404, 405, 409, 413, 415, 429, 500, 502, 503, 504)
        val messages = codes.associateWith { ApiErrors.messageForStatus(it) }
        messages.forEach { (code, message) -> assertTrue("Sin mensaje para $code", message.isNotBlank()) }
        assertEquals(codes.size, messages.values.toSet().size)
    }

    // CA-API-03: el mensaje mostrado lleva el código HTTP al inicio.
    @Test
    fun mensajeHttpLlevaCodigo() {
        assertTrue(httpError(404, "NOT_FOUND", "x").toUserMessage().startsWith("[HTTP-404] "))
    }

    // CA-API-04 [HTTP-502]: el SMTP no pudo enviar el código de recuperación.
    @Test
    fun error502EnRecuperacionExplicaQueFalloElCorreo() {
        val message = httpError(502, "BAD_GATEWAY", "Could not deliver the password recovery email",
            "/api/auth/password/recovery").toUserMessage()
        assertEquals("[HTTP-502] No se pudo enviar el correo con el código. Inténtalo de nuevo en unos minutos", message)
    }

    // CA-API-04 [HTTP-504]: el envío del correo tardó demasiado.
    @Test
    fun error504EnRecuperacionIndicaQueSePuedeReintentar() {
        val message = httpError(504, "GATEWAY_TIMEOUT", "timeout", "/api/auth/password/recovery").toUserMessage()
        assertTrue(message.startsWith("[HTTP-504]"))
        assertTrue(message.contains("tardó demasiado"))
    }

    // CA-API-04 [HTTP-400]: código de recuperación inválido o vencido.
    @Test
    fun error400ConCodigoDeRecuperacionInvalido() {
        val message = httpError(400, "BAD_REQUEST", "Invalid or expired recovery code").toUserMessage()
        assertEquals("[HTTP-400] El código es inválido o ya expiró. Solicita uno nuevo.", message)
    }

    // CA-API-02: un 400 de validación indica el primer campo que falló.
    @Test
    fun error400DeValidacionIndicaElCampo() {
        val dto = com.induztek.protocolos.data.remote.ApiErrorDto(code = "VALIDATION_ERROR",
            details = listOf(com.induztek.protocolos.data.remote.FieldDetailDto("email", "must be a well-formed email address")))
        assertEquals("Dato inválido en 'email': must be a well-formed email address", ApiErrors.messageForStatus(400, dto))
    }

    // CA-API-02: códigos no documentados usan un mensaje genérico con su número.
    @Test
    fun codigosDesconocidosUsanMensajeGenerico() {
        assertEquals("La solicitud no es válida (418)", ApiErrors.messageForStatus(418))
        assertEquals("Error del servidor (599)", ApiErrors.messageForStatus(599))
    }

    // CA-API-05 [RED-001, RED-002]: errores de red sin respuesta HTTP.
    @Test
    fun erroresDeRedSinRespuestaHttp() {
        assertCodigo(CodigoError.RED_001, SocketTimeoutException().toUserMessage())
        assertCodigo(CodigoError.RED_002, IOException().toUserMessage())
    }

    // CA-API-05 [APP-001]: una excepción no prevista no muestra detalles internos.
    @Test
    fun errorInesperado() {
        assertEquals("[APP-001] Ocurrió un error inesperado", IllegalStateException("NPE interno").toUserMessage())
    }

    // CA-API-05: una AppException conserva su propio código.
    @Test
    fun appExceptionConservaSuCodigo() {
        assertCodigo(CodigoError.RED_003, AppException(CodigoError.RED_003, 200).toUserMessage())
    }
}
