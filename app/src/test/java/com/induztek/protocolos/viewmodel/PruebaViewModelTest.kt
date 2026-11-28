package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Captura de una prueba nueva en terreno (PruebaViewModel).
 *
 * Criterios de aceptación:
 * - CA-PRU-01: al abrir la pantalla se selecciona el primer equipo disponible y su ubicación.
 * - CA-PRU-02: no se guarda sin equipo [PRU-001], sin OT [PRU-002] ni sin mediciones [PRU-003].
 * - CA-PRU-03: una medición sin nombre o sin valor no se agrega [PRU-004].
 * - CA-PRU-04: al cambiar el tipo de prueba se sugiere el instrumento y la plantilla de mediciones.
 * - CA-PRU-05: guardar crea la prueba y el protocolo, y actualiza el último estado del equipo.
 * - CA-PRU-06: el técnico queda registrado con nombre, rol y correo de la sesión.
 * - CA-PRU-07: los errores del backend se muestran con su código [HTTP-500].
 */
class PruebaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val equipo = Equipo(codigo = "TR-01", tipo = TipoEquipo.TRANSFORMADOR.nombre, cliente = "Planta Lampa", ubicacion = "Norte")
    private val listaEquipos = MutableStateFlow(listOf(equipo))

    private val equipos = mockk<EquipoRepository>(relaxed = true) {
        every { allEquipos } returns listaEquipos
        coEvery { getEquipoByCodigo("TR-01") } returns equipo
    }
    private val pruebas = mockk<PruebaRepository>(relaxed = true)
    private val protocolos = mockk<ProtocoloRepository>(relaxed = true)

    private fun crear() = PruebaViewModel(equipos, pruebas, protocolos)

    // CA-PRU-01: se preselecciona el primer equipo con su ubicación.
    @Test
    fun seleccionaElPrimerEquipo() {
        val vm = crear()
        assertEquals("TR-01", vm.uiState.value.equipoCodigo)
        assertEquals("Planta Lampa - Norte", vm.uiState.value.ubicacion)
    }

    // CA-PRU-02 [PRU-001]: sin equipos disponibles no se puede guardar.
    @Test
    fun guardarSinEquipo() {
        listaEquipos.value = emptyList()
        val vm = crear()
        var guardado = false
        vm.guardarProtocolo { guardado = true }
        assertFalse(guardado)
        assertCodigo(CodigoError.PRU_001, vm.remoteError.value)
    }

    // CA-PRU-02 [PRU-002]: la OT es obligatoria.
    @Test
    fun guardarSinOt() {
        val vm = crear()
        vm.onOrdenTrabajoChanged(" ")
        vm.guardarProtocolo {}
        assertCodigo(CodigoError.PRU_002, vm.remoteError.value)
    }

    // CA-PRU-02 [PRU-003]: debe haber al menos una medición.
    @Test
    fun guardarSinMediciones() {
        val vm = crear()
        repeat(vm.uiState.value.mediciones.size) { vm.eliminarMedicion(0) }
        vm.guardarProtocolo {}
        assertCodigo(CodigoError.PRU_003, vm.remoteError.value)
        coVerify(exactly = 0) { pruebas.insertPrueba(any()) }
    }

    // CA-PRU-03 [PRU-004]: una medición incompleta no se agrega.
    @Test
    fun medicionIncompletaNoSeAgrega() {
        val vm = crear()
        val antes = vm.uiState.value.mediciones.size
        vm.agregarMedicion("Tensión", " ")
        assertCodigo(CodigoError.PRU_004, vm.remoteError.value)
        assertEquals(antes, vm.uiState.value.mediciones.size)
    }

    // CA-PRU-03: una medición completa se agrega y limpia el error.
    @Test
    fun medicionCompletaSeAgrega() {
        val vm = crear()
        val antes = vm.uiState.value.mediciones.size
        vm.agregarMedicion("Tensión", "5000 V")
        assertEquals(antes + 1, vm.uiState.value.mediciones.size)
        assertEquals(5000.0, vm.uiState.value.mediciones.last().valorNumerico!!, 0.0)
        assertNull(vm.remoteError.value)
    }

    // CA-PRU-04: TTR sugiere el Vanguard y su plantilla de 3 mediciones.
    @Test
    fun cambiarTipoSugiereInstrumentoYPlantilla() {
        val vm = crear()
        vm.onTipoPruebaChanged("Relación de Transformación (TTR)")
        assertTrue(vm.uiState.value.instrumento.startsWith("Vanguard"))
        assertEquals(3, vm.uiState.value.mediciones.size)
    }

    // CA-PRU-05: guardar crea prueba y protocolo y actualiza el estado del equipo.
    @Test
    fun guardarCorrecto() {
        val vm = crear()
        vm.onEstadoChanged(EstadoProtocolo.OBSERVADO)
        var guardado = false
        vm.guardarProtocolo { guardado = true }
        assertTrue(guardado)
        coVerify { pruebas.insertPrueba(match { it.equipoCodigo == "TR-01" }) }
        coVerify { protocolos.insertProtocolo(match { it.estado == EstadoProtocolo.OBSERVADO }) }
        coVerify { equipos.insertEquipo(match { it.ultimoEstado == EstadoProtocolo.OBSERVADO }) }
    }

    // CA-PRU-06: el técnico queda identificado con los datos de la sesión.
    @Test
    fun tecnicoDeLaSesion() {
        val vm = crear()
        vm.setUsuarioSesion("Juan", "juan@induztek.cl", "P-01")
        assertEquals("Juan (P-01 - juan@induztek.cl)", vm.uiState.value.tecnico)
    }

    // CA-PRU-07 [HTTP-500]: el backend falla al guardar.
    @Test
    fun errorDelBackendAlGuardar() {
        coEvery { pruebas.insertPrueba(any()) } throws httpError(500, "INTERNAL_ERROR", "Unexpected")
        val vm = crear()
        var guardado = false
        vm.guardarProtocolo { guardado = true }
        assertFalse(guardado)
        assertHttp(500, vm.remoteError.value)
    }
}
