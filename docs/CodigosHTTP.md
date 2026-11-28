# Códigos HTTP en la app Android

La app recibe del backend (vía API Gateway) los códigos de esta tabla. Toda la traducción a mensajes para el usuario está centralizada en
[`data/remote/ApiErrors.kt`](../app/src/main/java/com/induztek/protocolos/data/remote/ApiErrors.kt):

- `ApiErrors.parse(HttpException)` lee el cuerpo JSON `ApiError` del backend (`status`, `code`, `message`, `path`, `details`).
- `ApiErrors.messageForStatus(status, apiError)` devuelve el mensaje en español.
- `Throwable.toUserMessage()` es lo que usan los ViewModels (`LoginViewModel.errorMessage` y `launchCatching` en `RemoteViewModelExtensions.kt`).

> El cuerpo de error de OkHttp solo se puede leer **una vez**: llamar a `parse` / `toUserMessage` una sola vez por excepción.

## Respuestas exitosas (2xx)

Retrofit las trata como éxito (`response.isSuccessful`). `requireBody()` exige cuerpo; `requireSuccess()` no.

| HTTP | Significado | Endpoints de la app |
|------|-------------|---------------------|
| 200 | OK, el cuerpo trae el recurso | login, `GET api/usuarios/me`, listados, `PATCH` de usuarios, `POST api/imagenes` |
| 201 | Created, recurso creado | registro, equipos, instrumentos, visitas, protocolos, pruebas, actas |
| 202 | Accepted | `POST api/auth/password/recovery` (código enviado al correo) |
| 204 | No Content (sin cuerpo; usar `Response<Unit>` + `requireSuccess()`) | `POST api/auth/password/verify`, `DELETE api/usuarios/{id}` |

## Errores (4xx y 5xx)

Retrofit lanza `retrofit2.HttpException` (vía `requireBody()` / `requireSuccess()`).

| HTTP | `code` backend | Cuándo ocurre | Mensaje en la app |
|------|----------------|---------------|-------------------|
| 400 | `VALIDATION_ERROR` | Un campo no cumple las reglas | "Dato inválido en '&lt;campo&gt;': …" (usa `details`) |
| 400 | `BAD_REQUEST` (código de recuperación) | Código inválido, vencido o agotado | "El código es inválido o ya expiró. Solicita uno nuevo." |
| 400 | otros (`MALFORMED_REQUEST`, `INVALID_PARAMETER`, …) | Cuerpo o parámetros inválidos | "Los datos enviados no son válidos" |
| 401 | `UNAUTHORIZED` / sin cuerpo | Credenciales incorrectas o token JWT vencido | "Correo o contraseña incorrectos, o tu sesión expiró" |
| 403 | `FORBIDDEN` | Rol sin permiso, cuenta pendiente o desactivada | "No tienes permiso, o la cuenta está pendiente…" |
| 404 | `NOT_FOUND` | El recurso no existe. Los repositorios de visitas, equipos, protocolos, pruebas y actas lo convierten en `null` en vez de error | "No se encontró el recurso solicitado" |
| 405 / 406 | `METHOD_NOT_ALLOWED` / `NOT_ACCEPTABLE` | Error de programación de la app (método o formato incorrecto) | "La app hizo una solicitud no soportada…" |
| 409 | `CONFLICT` | Duplicado (correo, código de equipo, acta repetida, protocolo ya formalizado) | "El registro ya existe o entra en conflicto con otro" |
| 413 | `PAYLOAD_TOO_LARGE` | Imagen de más de 10 MB | "La imagen supera el tamaño máximo de 10 MB" |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | Imagen que no es JPEG/PNG | "Formato de archivo no soportado…" |
| 429 | `TOO_MANY_REQUESTS` | 5 logins fallidos: bloqueo de 15 min | "Demasiados intentos fallidos. Espera 15 minutos…" |
| 500 | `INTERNAL_ERROR` | Error inesperado del backend | "Error interno del servidor…" |
| 502 | `BAD_GATEWAY` | En recuperación de contraseña: el SMTP no pudo enviar el correo. En otros endpoints: el gateway o el túnel no llegó al microservicio | Recuperación: "No se pudo enviar el correo con el código…" |
| 503 | `SERVICE_UNAVAILABLE` | Un microservicio del que depende la operación no responde | "El servicio no está disponible en este momento…" |
| 504 | `GATEWAY_TIMEOUT` | El envío del correo de recuperación tardó más de 50 s | "El envío del correo tardó demasiado. Inténtalo de nuevo" |
| otro 4xx / 5xx | — | No documentado | "La solicitud no es válida (N)" / "Error del servidor (N)" |

## Errores sin respuesta HTTP

| Excepción | Cuándo | Mensaje |
|-----------|--------|---------|
| `SocketTimeoutException` | El servidor no respondió en `RetrofitClient.READ_TIMEOUT_SECONDS` (60 s) | "El servidor tardó demasiado en responder…" |
| `IOException` | Sin red, túnel caído, DNS | "No fue posible conectar con el servidor…" |

## Pruebas

Los mensajes están cubiertos por `ApiErrorsTest`, `AuthApiRecoveryTest` (MockWebServer) y `LoginViewModelRecuperacionTest`.

```powershell
.\gradlew.bat :app:testDebugUnitTest                    # tests unitarios (muestra cada test en consola)
.\gradlew.bat :app:createDebugUnitTestCoverageReport    # cobertura -> app/build/reports/coverage/test/debug/index.html
```

Utilidades de test en `app/src/test/java/com/induztek/protocolos/testutil/`:

- `MainDispatcherRule`: reemplaza `Dispatchers.Main` para probar ViewModels (`@get:Rule val rule = MainDispatcherRule()`).
- `httpError(status, code, message, path)`: crea un `HttpException` con el cuerpo `ApiError` del backend.
