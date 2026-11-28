package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelRecuperacionTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UsuarioRepository>(relaxed = true)
    private val viewModel = LoginViewModel(usuarioRepository = repository)

    @Test
    fun solicitarCodigoExitosoPasaACodigoEnviado() {
        viewModel.solicitarCodigo("user@induztek.cl")

        assertEquals(RecuperacionUiState.CodigoEnviado, viewModel.recuperacionState.value)
        coVerify { repository.solicitarRecuperacion("user@induztek.cl") }
    }

    @Test
    fun solicitarCodigoConCorreoInvalidoNoLlamaAlBackend() {
        viewModel.solicitarCodigo("correo-invalido")

        assertEquals(RecuperacionUiState.Error("Ingresa un correo válido"), viewModel.recuperacionState.value)
        coVerify(exactly = 0) { repository.solicitarRecuperacion(any()) }
    }

    @Test
    fun error502AlEnviarCorreoMuestraMensajeClaro() {
        coEvery { repository.solicitarRecuperacion(any()) } throws httpError(
            502, "BAD_GATEWAY", "Could not deliver the password recovery email", "/api/auth/password/recovery"
        )

        viewModel.solicitarCodigo("user@induztek.cl")

        val state = viewModel.recuperacionState.value as RecuperacionUiState.Error
        assertTrue(state.message.contains("No se pudo enviar el correo"))
    }

    @Test
    fun codigoInvalidoMuestraMensajeDeCodigoExpirado() {
        coEvery { repository.verificarCodigo(any(), any()) } throws httpError(
            400, "BAD_REQUEST", "Invalid or expired recovery code", "/api/auth/password/verify"
        )
        viewModel.onCorreoRecuperacionChanged("user@induztek.cl")

        viewModel.verificarCodigo("123456")

        assertEquals(
            RecuperacionUiState.Error("El código es inválido o ya expiró. Solicita uno nuevo."),
            viewModel.recuperacionState.value
        )
    }

    @Test
    fun actualizarContrasenaExigeCodigoVerificado() {
        viewModel.actualizarContrasena("Segura#2026x")

        assertEquals(
            RecuperacionUiState.Error("Primero verifica el código recibido"),
            viewModel.recuperacionState.value
        )
    }
}
