package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Recuperación de contraseña (LoginViewModel).
 *
 * Criterios de aceptación:
 * - CA-REC-01: con correo válido se pide el código y el estado pasa a CodigoEnviado.
 * - CA-REC-02: con correo inválido no se llama al backend [AUTH-001].
 * - CA-REC-03: si falla el envío del correo se explica el motivo [HTTP-502] / [HTTP-504].
 * - CA-REC-04: el código debe tener 6 dígitos [AUTH-020]; uno inválido o vencido da [HTTP-400].
 * - CA-REC-05: no se cambia la contraseña sin verificar el código [AUTH-021].
 * - CA-REC-06: la nueva contraseña debe ser segura [AUTH-010..014] y coincidir con la confirmación [AUTH-022].
 * - CA-REC-07: con código verificado y contraseña válida se actualiza y se deja listo el login.
 */
class LoginViewModelRecuperacionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UsuarioRepository>(relaxed = true)
    private val viewModel = LoginViewModel(usuarioRepository = repository)

    private fun error(): String? = (viewModel.recuperacionState.value as? RecuperacionUiState.Error)?.message

    private fun verificarCodigoValido() {
        viewModel.onCorreoRecuperacionChanged("user@induztek.cl")
        viewModel.onCodigoIngresadoChanged("123456")
        viewModel.verificarCodigo()
    }

    // CA-REC-01: correo válido → se envía el código.
    @Test
    fun solicitarCodigoExitosoPasaACodigoEnviado() {
        viewModel.solicitarCodigo("user@induztek.cl")
        assertEquals(RecuperacionUiState.CodigoEnviado, viewModel.recuperacionState.value)
        coVerify { repository.solicitarRecuperacion("user@induztek.cl") }
    }

    // CA-REC-02 [AUTH-001]: correo inválido no llega al backend.
    @Test
    fun solicitarCodigoConCorreoInvalidoNoLlamaAlBackend() {
        viewModel.solicitarCodigo("correo-invalido")
        assertCodigo(CodigoError.AUTH_001, error())
        coVerify(exactly = 0) { repository.solicitarRecuperacion(any()) }
    }

    // CA-REC-03 [HTTP-502]: el SMTP no pudo enviar el correo.
    @Test
    fun error502AlEnviarCorreoMuestraMensajeClaro() {
        coEvery { repository.solicitarRecuperacion(any()) } throws httpError(
            502, "BAD_GATEWAY", "Could not deliver the password recovery email", "/api/auth/password/recovery"
        )
        viewModel.solicitarCodigo("user@induztek.cl")
        assertHttp(502, error())
        assertTrue(error()!!.contains("No se pudo enviar el correo"))
    }

    // CA-REC-03 [HTTP-504]: el envío del correo tardó demasiado.
    @Test
    fun error504AlEnviarCorreoIndicaReintentar() {
        coEvery { repository.solicitarRecuperacion(any()) } throws httpError(
            504, "GATEWAY_TIMEOUT", "timeout", "/api/auth/password/recovery"
        )
        viewModel.solicitarCodigo("user@induztek.cl")
        assertHttp(504, error())
    }

    // CA-REC-04 [AUTH-020]: un código que no tiene 6 dígitos se rechaza localmente.
    @Test
    fun codigoConFormatoInvalido() {
        viewModel.verificarCodigo("12ab")
        assertCodigo(CodigoError.AUTH_020, error())
        coVerify(exactly = 0) { repository.verificarCodigo(any(), any()) }
    }

    // CA-REC-04 [HTTP-400]: el backend rechaza un código inválido o vencido.
    @Test
    fun codigoInvalidoMuestraMensajeDeCodigoExpirado() {
        coEvery { repository.verificarCodigo(any(), any()) } throws httpError(
            400, "BAD_REQUEST", "Invalid or expired recovery code", "/api/auth/password/verify"
        )
        viewModel.onCorreoRecuperacionChanged("user@induztek.cl")
        viewModel.verificarCodigo("123456")
        assertHttp(400, error())
        assertTrue(error()!!.contains("inválido o ya expiró"))
    }

    // CA-REC-05 [AUTH-021]: sin verificar el código no se cambia la contraseña.
    @Test
    fun actualizarContrasenaExigeCodigoVerificado() {
        viewModel.actualizarContrasena("Segura#2026x")
        assertCodigo(CodigoError.AUTH_021, error())
    }

    // CA-REC-06 [AUTH-010]: la nueva contraseña debe cumplir los requisitos.
    @Test
    fun nuevaContrasenaDebil() {
        verificarCodigoValido()
        viewModel.onNuevaContrasenaChanged("debil")
        viewModel.actualizarContrasena()
        assertCodigo(CodigoError.AUTH_010, error())
    }

    // CA-REC-06 [AUTH-022]: la confirmación debe coincidir.
    @Test
    fun confirmacionDistinta() {
        verificarCodigoValido()
        viewModel.onNuevaContrasenaChanged("Segura#2026x")
        viewModel.onConfirmarContrasenaChanged("Otra#2026xx")
        viewModel.actualizarContrasena()
        assertCodigo(CodigoError.AUTH_022, error())
    }

    // CA-REC-07: flujo completo correcto deja el correo y la contraseña listos para iniciar sesión.
    @Test
    fun flujoCompletoActualizaContrasena() {
        verificarCodigoValido()
        assertEquals(RecuperacionUiState.CodigoVerificado, viewModel.recuperacionState.value)
        viewModel.onNuevaContrasenaChanged("Segura#2026x")
        viewModel.onConfirmarContrasenaChanged("Segura#2026x")
        viewModel.actualizarContrasena()
        assertTrue(viewModel.recuperacionState.value is RecuperacionUiState.Success)
        assertEquals("user@induztek.cl", viewModel.email.value)
        coVerify { repository.actualizarContrasena("user@induztek.cl", "123456", "Segura#2026x") }
    }
}
