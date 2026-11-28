package com.induztek.protocolos

import com.induztek.protocolos.data.remote.ApiErrors
import com.induztek.protocolos.data.remote.toUserMessage
import com.induztek.protocolos.testutil.httpError
import java.io.IOException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiErrorsTest {

    @Test
    fun parseLeeElCuerpoApiErrorDelBackend() {
        val error = ApiErrors.parse(httpError(404, "NOT_FOUND", "Account not found", "/api/usuarios/me"))!!
        assertEquals(404, error.status)
        assertEquals("NOT_FOUND", error.code)
        assertEquals("Account not found", error.message)
        assertEquals("/api/usuarios/me", error.path)
    }

    @Test
    fun parseDevuelveNullSiNoHayCuerpoJson() {
        assertNull(ApiErrors.parse(httpError(401)))
    }

    @Test
    fun cadaCodigoDeErrorDocumentadoTieneMensajePropio() {
        val codes = listOf(400, 401, 403, 404, 405, 409, 413, 415, 429, 500, 502, 503, 504)
        val messages = codes.associateWith { ApiErrors.messageForStatus(it) }
        messages.forEach { (code, message) -> assertTrue("Sin mensaje para $code", message.isNotBlank()) }
        // 405 y 406 comparten mensaje; el resto debe distinguirse.
        assertEquals(codes.size, messages.values.toSet().size)
    }

    @Test
    fun error502EnRecuperacionExplicaQueFalloElCorreo() {
        val message = httpError(502, "BAD_GATEWAY", "Could not deliver the password recovery email",
            "/api/auth/password/recovery").toUserMessage()
        assertEquals("No se pudo enviar el correo con el código. Inténtalo de nuevo en unos minutos", message)
    }

    @Test
    fun error504EnRecuperacionIndicaQueSePuedeReintentar() {
        val message = httpError(504, "GATEWAY_TIMEOUT", "timeout", "/api/auth/password/recovery").toUserMessage()
        assertTrue(message.contains("tardó demasiado"))
    }

    @Test
    fun error400ConCodigoDeRecuperacionInvalido() {
        val message = httpError(400, "BAD_REQUEST", "Invalid or expired recovery code").toUserMessage()
        assertEquals("El código es inválido o ya expiró. Solicita uno nuevo.", message)
    }

    @Test
    fun codigosDesconocidosUsanMensajeGenerico() {
        assertEquals("La solicitud no es válida (418)", ApiErrors.messageForStatus(418))
        assertEquals("Error del servidor (599)", ApiErrors.messageForStatus(599))
    }

    @Test
    fun erroresDeRedSinRespuestaHttp() {
        assertTrue(SocketTimeoutException().toUserMessage().contains("tardó demasiado"))
        assertTrue(IOException().toUserMessage().contains("No fue posible conectar"))
    }
}
