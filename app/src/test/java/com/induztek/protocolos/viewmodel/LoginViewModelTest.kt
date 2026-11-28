package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.LoginSession
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Inicio de sesión, registro, reglas de contraseña y perfil (LoginViewModel).
 *
 * Criterios de aceptación:
 * - CA-LOG-01: con correo inválido no se llama al backend [AUTH-001].
 * - CA-LOG-02: con contraseña vacía no se llama al backend [AUTH-002].
 * - CA-LOG-03: sin repositorio configurado se informa el problema [AUTH-003].
 * - CA-LOG-04: login correcto con cuenta activa deja el estado Success y el usuario activo.
 * - CA-LOG-05: cuenta desactivada no inicia sesión [AUTH-004].
 * - CA-LOG-06: credenciales incorrectas o bloqueo se muestran con su código HTTP [HTTP-401, HTTP-429].
 * - CA-LOG-07: sin red se muestra un error de conexión [RED-002].
 * - CA-REGI-01: el registro exige nombre [AUTH-005], correo válido [AUTH-001], contraseña segura [AUTH-010..014] y términos [AUTH-006].
 * - CA-REGI-02: el registro público siempre crea una cuenta CLIENT; si queda pendiente se avisa.
 * - CA-REGI-03: un correo ya registrado se informa con HTTP-409.
 * - CA-PASS-01: cada requisito de la contraseña tiene su propio código.
 * - CA-PERF-01: editar el perfil sin sesión activa da PERF-001.
 * - CA-SES-01: cerrar sesión limpia el usuario y las credenciales.
 */
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UsuarioRepository>(relaxed = true)
    private val viewModel = LoginViewModel(usuarioRepository = repository)

    private fun perfil(activo: Boolean = true) =
        UserProfile(id = "USR-1", correo = "user@induztek.cl", nombre = "Usuario", perfilCodigo = "P-01", activo = activo)

    private fun loginCon(correo: String, clave: String) {
        viewModel.onEmailChanged(correo)
        viewModel.onPasswordChanged(clave)
        viewModel.login()
    }

    private fun errorLogin(): String? = (viewModel.uiState.value as? LoginUiState.Error)?.message
    private fun errorRegistro(): String? = (viewModel.registroState.value as? RegistroUiState.Error)?.message

    // CA-LOG-01 [AUTH-001]: un correo sin dominio se rechaza localmente.
    @Test
    fun loginConCorreoInvalido() {
        loginCon("usuario-sin-arroba", "Clave#2026x")
        assertCodigo(CodigoError.AUTH_001, errorLogin())
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    // CA-LOG-02 [AUTH-002]: la contraseña vacía se rechaza localmente.
    @Test
    fun loginConContrasenaVacia() {
        loginCon("user@induztek.cl", "")
        assertCodigo(CodigoError.AUTH_002, errorLogin())
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    // CA-LOG-03 [AUTH-003]: sin UsuarioRepository no se puede iniciar sesión.
    @Test
    fun loginSinRepositorio() {
        val sinRepo = LoginViewModel()
        sinRepo.onEmailChanged("user@induztek.cl")
        sinRepo.onPasswordChanged("Clave#2026x")
        sinRepo.login()
        assertCodigo(CodigoError.AUTH_003, (sinRepo.uiState.value as LoginUiState.Error).message)
    }

    // CA-LOG-04: login correcto deja Success y carga el usuario activo.
    @Test
    fun loginExitoso() {
        coEvery { repository.login(any(), any()) } returns LoginSession("token", perfil())
        loginCon("user@induztek.cl", "Clave#2026x")
        assertEquals(LoginUiState.Success("user@induztek.cl"), viewModel.uiState.value)
        assertEquals("USR-1", viewModel.usuarioActivo.value?.id)
    }

    // CA-LOG-05 [AUTH-004]: una cuenta desactivada no queda con sesión.
    @Test
    fun loginConCuentaDesactivada() {
        coEvery { repository.login(any(), any()) } returns LoginSession("token", perfil(activo = false))
        loginCon("user@induztek.cl", "Clave#2026x")
        assertCodigo(CodigoError.AUTH_004, errorLogin())
        assertNull(viewModel.usuarioActivo.value)
    }

    // CA-LOG-06 [HTTP-401]: credenciales incorrectas.
    @Test
    fun loginConCredencialesIncorrectas() {
        coEvery { repository.login(any(), any()) } throws httpError(401, "UNAUTHORIZED", "Invalid credentials", "/api/auth/login")
        loginCon("user@induztek.cl", "Clave#2026x")
        assertHttp(401, errorLogin())
    }

    // CA-LOG-06 [HTTP-429]: bloqueo tras 5 intentos fallidos.
    @Test
    fun loginBloqueadoPorIntentos() {
        coEvery { repository.login(any(), any()) } throws httpError(429, "TOO_MANY_REQUESTS", "Too many attempts", "/api/auth/login")
        loginCon("user@induztek.cl", "Clave#2026x")
        assertHttp(429, errorLogin())
        assertTrue(errorLogin()!!.contains("15 minutos"))
    }

    // CA-LOG-07 [RED-002]: sin conexión con el servidor.
    @Test
    fun loginSinConexion() {
        coEvery { repository.login(any(), any()) } throws IOException("offline")
        loginCon("user@induztek.cl", "Clave#2026x")
        assertCodigo(CodigoError.RED_002, errorLogin())
    }

    // CA-REGI-01 [AUTH-005]: el nombre es obligatorio.
    @Test
    fun registroSinNombre() {
        viewModel.registrar(" ", "user@induztek.cl", "Clave#2026x", "Planta", true)
        assertCodigo(CodigoError.AUTH_005, errorRegistro())
    }

    // CA-REGI-01 [AUTH-001]: el correo debe ser válido.
    @Test
    fun registroConCorreoInvalido() {
        viewModel.registrar("Ana", "ana@", "Clave#2026x", "Planta", true)
        assertCodigo(CodigoError.AUTH_001, errorRegistro())
    }

    // CA-REGI-01 [AUTH-010]: la contraseña debe ser segura.
    @Test
    fun registroConContrasenaDebil() {
        viewModel.registrar("Ana", "ana@induztek.cl", "corta", "Planta", true)
        assertCodigo(CodigoError.AUTH_010, errorRegistro())
    }

    // CA-REGI-01 [AUTH-006]: hay que aceptar los términos.
    @Test
    fun registroSinAceptarTerminos() {
        viewModel.registrar("Ana", "ana@induztek.cl", "Clave#2026x", "Planta", false)
        assertCodigo(CodigoError.AUTH_006, errorRegistro())
        coVerify(exactly = 0) { repository.registerWithRoleAndCompany(any(), any(), any(), any(), any(), any()) }
    }

    // CA-REGI-02: el registro público crea siempre un CLIENT y avisa si queda pendiente de aprobación.
    @Test
    fun registroExitosoPendienteDeAprobacion() {
        coEvery { repository.registerWithRoleAndCompany(any(), any(), any(), any(), any(), any()) } returns perfil(activo = false)
        viewModel.registrar("Ana", "ana@induztek.cl", "Clave#2026x", "Planta Lampa", true)
        val estado = viewModel.registroState.value as RegistroUiState.Success
        assertTrue(estado.message.contains("administrador debe aprobar"))
        coVerify { repository.registerWithRoleAndCompany("Ana", "ana@induztek.cl", "Clave#2026x", "Planta Lampa", "CLIENT", true) }
    }

    // CA-REGI-03 [HTTP-409]: correo ya registrado.
    @Test
    fun registroConCorreoDuplicado() {
        coEvery { repository.registerWithRoleAndCompany(any(), any(), any(), any(), any(), any()) } throws
            httpError(409, "CONFLICT", "Email already registered", "/api/auth/register")
        viewModel.registrar("Ana", "ana@induztek.cl", "Clave#2026x", "Planta", true)
        assertHttp(409, errorRegistro())
    }

    // CA-PASS-01 [AUTH-010..014]: cada requisito incumplido devuelve su propio código.
    @Test
    fun cadaRequisitoDeContrasenaTieneSuCodigo() {
        assertCodigo(CodigoError.AUTH_010, viewModel.mensajeValidacionContrasena("Ab1#"))
        assertCodigo(CodigoError.AUTH_011, viewModel.mensajeValidacionContrasena("clave#2026x"))
        assertCodigo(CodigoError.AUTH_012, viewModel.mensajeValidacionContrasena("CLAVE#2026X"))
        assertCodigo(CodigoError.AUTH_013, viewModel.mensajeValidacionContrasena("Clave#Segura"))
        assertCodigo(CodigoError.AUTH_014, viewModel.mensajeValidacionContrasena("Clave2026xy"))
        assertNull(viewModel.mensajeValidacionContrasena("Clave#2026x"))
    }

    // CA-PERF-01 [PERF-001]: sin usuario activo no se puede editar el perfil.
    @Test
    fun editarPerfilSinSesion() {
        viewModel.actualizarBiografia("Nueva bio")
        assertCodigo(CodigoError.PERF_001, viewModel.profileError.value)
        coVerify(exactly = 0) { repository.actualizarBiografia(any(), any()) }
    }

    // CA-PERF-01: con sesión, actualizar la biografía la guarda y limpia el error.
    @Test
    fun editarPerfilConSesion() {
        coEvery { repository.login(any(), any()) } returns LoginSession("token", perfil())
        loginCon("user@induztek.cl", "Clave#2026x")
        viewModel.actualizarBiografia("Nueva bio")
        assertEquals("Nueva bio", viewModel.usuarioActivo.value?.biografia)
        assertNull(viewModel.profileError.value)
    }

    // CA-SES-01: cerrar sesión deja todo limpio.
    @Test
    fun cerrarSesionLimpiaElEstado() {
        coEvery { repository.login(any(), any()) } returns LoginSession("token", perfil())
        loginCon("user@induztek.cl", "Clave#2026x")
        viewModel.cerrarSesion()
        assertNull(viewModel.usuarioActivo.value)
        assertEquals("", viewModel.email.value)
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
        assertFalse(viewModel.isLoginInputValid)
    }
}
