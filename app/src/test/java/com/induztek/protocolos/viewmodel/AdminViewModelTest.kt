package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Administración de instrumentos y personal (AdminViewModel).
 *
 * Criterios de aceptación:
 * - CA-ADM-01: un instrumento exige marca, modelo y número de serie [ADM-001].
 * - CA-ADM-02: la fecha de calibración debe existir y tener formato AAAA-MM-DD [ADM-002].
 * - CA-ADM-03: un instrumento válido se guarda activo, con id INST-* y el formulario se limpia.
 * - CA-ADM-04: crear usuario exige nombre, correo y contraseña de 10+ caracteres [ADM-003] y correo válido [ADM-004].
 * - CA-ADM-05: un usuario válido se crea con el rol elegido y se refresca la lista.
 * - CA-ADM-06: activar/desactivar invierte el estado actual del usuario.
 * - CA-ADM-07: los errores del backend se muestran con su código [HTTP-403, HTTP-409, RED-001].
 */
class AdminViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val instrumentos = mockk<InstrumentoRepository>(relaxed = true) {
        every { allInstrumentos } returns MutableStateFlow(emptyList())
    }
    private val usuarios = mockk<UsuarioRepository>(relaxed = true) {
        every { allUsuarios } returns MutableStateFlow(emptyList())
    }
    // by lazy: el ViewModel usa viewModelScope al construirse y debe crearse después de MainDispatcherRule.
    private val viewModel by lazy { AdminViewModel(instrumentos, usuarios) }

    private fun completarInstrumento(fecha: String = "2027-12-31") {
        viewModel.onMarcaChanged("Megger")
        viewModel.onModeloChanged("MIT525")
        viewModel.onNumeroSerieChanged("SN-001")
        viewModel.onVencimientoCalibracionChanged(fecha)
    }

    private val usuario = UserProfile(id = "USR-1", correo = "a@induztek.cl", nombre = "Ana", perfilCodigo = "P-01", activo = true)

    // CA-ADM-01 [ADM-001]: sin marca/modelo/serie no se guarda el instrumento.
    @Test
    fun instrumentoSinCamposObligatorios() {
        viewModel.crearInstrumento {}
        assertCodigo(CodigoError.ADM_001, viewModel.nuevoInstrumentoState.value.errorValidacion)
        coVerify(exactly = 0) { instrumentos.insertInstrumento(any()) }
    }

    // CA-ADM-02 [ADM-002]: una fecha con formato incorrecto se rechaza.
    @Test
    fun instrumentoConFechaMalFormateada() {
        completarInstrumento("31/12/2027")
        viewModel.crearInstrumento {}
        assertCodigo(CodigoError.ADM_002, viewModel.nuevoInstrumentoState.value.errorValidacion)
    }

    // CA-ADM-02 [ADM-002]: una fecha que no existe en el calendario se rechaza.
    @Test
    fun instrumentoConFechaInexistente() {
        completarInstrumento("2027-02-30")
        viewModel.crearInstrumento {}
        assertCodigo(CodigoError.ADM_002, viewModel.nuevoInstrumentoState.value.errorValidacion)
    }

    // CA-ADM-03: un instrumento válido se guarda activo y se limpia el formulario.
    @Test
    fun instrumentoValidoSeGuarda() {
        val guardado = slot<Instrumento>()
        coEvery { instrumentos.insertInstrumento(capture(guardado)) } returns Unit
        var exito = false
        completarInstrumento()
        viewModel.crearInstrumento { exito = true }
        assertTrue(exito)
        assertTrue(guardado.captured.id.startsWith("INST-"))
        assertTrue(guardado.captured.activo)
        assertEquals("", viewModel.nuevoInstrumentoState.value.marca)
    }

    // CA-ADM-07 [HTTP-409]: número de serie duplicado en el backend.
    @Test
    fun instrumentoDuplicadoEnBackend() {
        coEvery { instrumentos.insertInstrumento(any()) } throws httpError(409, "CONFLICT", "Duplicate")
        completarInstrumento()
        viewModel.crearInstrumento {}
        assertHttp(409, viewModel.error.value)
    }

    // CA-ADM-04 [ADM-003]: contraseña de menos de 10 caracteres.
    @Test
    fun usuarioConContrasenaCorta() {
        viewModel.crearUsuario("Ana", "ana@induztek.cl", "corta", "Planta", "TECHNICIAN") {}
        assertCodigo(CodigoError.ADM_003, viewModel.error.value)
    }

    // CA-ADM-04 [ADM-004]: correo con formato inválido.
    @Test
    fun usuarioConCorreoInvalido() {
        viewModel.crearUsuario("Ana", "ana-induztek.cl", "Clave#2026x", "Planta", "TECHNICIAN") {}
        assertCodigo(CodigoError.ADM_004, viewModel.error.value)
        coVerify(exactly = 0) { usuarios.registerWithRoleAndCompany(any(), any(), any(), any(), any(), any()) }
    }

    // CA-ADM-05: usuario válido se crea con su rol y se refresca la lista.
    @Test
    fun usuarioValidoSeCrea() {
        var exito = false
        viewModel.crearUsuario(" Ana ", "ana@induztek.cl", "Clave#2026x", " ", "ENGINEER") { exito = true }
        assertTrue(exito)
        assertNull(viewModel.error.value)
        coVerify { usuarios.registerWithRoleAndCompany("Ana", "ana@induztek.cl", "Clave#2026x", null, "ENGINEER", true) }
        coVerify { usuarios.refresh() }
    }

    // CA-ADM-06: activar/desactivar invierte el estado.
    @Test
    fun toggleActivoInvierteElEstado() {
        viewModel.toggleActivoPersonal(usuario)
        coVerify { usuarios.setActivo("USR-1", false) }
    }

    // CA-ADM-07 [HTTP-403]: sin permiso para cambiar el rol.
    @Test
    fun cambiarRolSinPermiso() {
        coEvery { usuarios.actualizarRol(any(), any()) } throws httpError(403, "FORBIDDEN", "Forbidden")
        viewModel.cambiarRolPersonal(usuario, "ADMIN")
        assertHttp(403, viewModel.error.value)
    }

    // CA-ADM-07 [RED-001]: el servidor no responde al cargar los datos.
    @Test
    fun refreshConTimeout() {
        coEvery { instrumentos.refresh() } throws SocketTimeoutException()
        viewModel.refresh()
        assertCodigo(CodigoError.RED_001, viewModel.error.value)
    }

    // CA-ADM-07: eliminar un usuario correctamente no deja error.
    @Test
    fun eliminarUsuarioCorrecto() {
        viewModel.eliminarUsuario("USR-1")
        coVerify { usuarios.eliminarUsuario("USR-1") }
        assertNull(viewModel.error.value)
        assertFalse(viewModel.nuevoInstrumentoState.value.errorValidacion != null)
    }
}
