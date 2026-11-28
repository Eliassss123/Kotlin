package com.induztek.protocolos.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Contrato HTTP de la recuperación de contraseña contra un servidor simulado (MockWebServer). */
class AuthApiRecoveryTest {
    private lateinit var server: MockWebServer
    private lateinit var api: AuthApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun recovery202DevuelveMensaje() = runTest {
        server.enqueue(MockResponse().setResponseCode(202)
            .setBody("""{"message":"If the account is active, a recovery code has been sent."}"""))

        val body = api.requestPasswordRecovery(RecoveryRequest("user@induztek.cl")).requireBody()

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/auth/password/recovery", request.path)
        assertTrue(request.body.readUtf8().contains("user@induztek.cl"))
        assertTrue(body.message.contains("recovery code"))
    }

    @Test
    fun recovery502LanzaHttpExceptionConCuerpoApiError() = runTest {
        server.enqueue(MockResponse().setResponseCode(502).setBody(
            """{"status":502,"error":"Bad Gateway","code":"BAD_GATEWAY","message":"Could not deliver the password recovery email","path":"/api/auth/password/recovery","details":[]}"""
        ))

        val exception = runCatching {
            api.requestPasswordRecovery(RecoveryRequest("user@induztek.cl")).requireBody()
        }.exceptionOrNull() as HttpException

        assertEquals(502, exception.code())
        // El cuerpo de error solo se puede leer una vez: se parsea una sola vez.
        val apiError = ApiErrors.parse(exception)
        assertEquals("BAD_GATEWAY", apiError?.code)
        assertTrue(ApiErrors.messageForStatus(502, apiError).contains("No se pudo enviar el correo"))
    }

    @Test
    fun verify204EsExitoso() = runTest {
        server.enqueue(MockResponse().setResponseCode(204))

        api.verifyRecoveryCode(VerifyResetCodeRequest("user@induztek.cl", "123456")).requireSuccess()

        assertEquals("/api/auth/password/verify", server.takeRequest().path)
    }
}
