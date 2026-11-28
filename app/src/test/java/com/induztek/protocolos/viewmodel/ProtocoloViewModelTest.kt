package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Detalle de protocolos, edición de pruebas y formalización (ProtocoloViewModel).
 *
 * Criterios de aceptación:
 * - CA-PRO-01: un protocolo o prueba inexistente se informa [PRO-004, PRO-005].
 * - CA-PRO-02: guardar sin prueba en edición [PRO-002] o sin protocolo cargado [PRO-003] se informa.
 * - CA-PRO-03: una prueba NO REALIZADA exige motivo (Aclaración 2) [PRO-001] y su resultado pasa a NO_REALIZADA.
 * - CA-PRO-04: en aislación el IP se recalcula al cambiar las lecturas y define el resultado.
 * - CA-PRO-05: al guardar una prueba se recalcula el resultado global del protocolo (el peor).
 * - CA-PRO-06: un instrumento con calibración vencida muestra advertencia.
 * - CA-PRO-07: un protocolo FORMALIZADO no se puede editar ni formalizar otra vez [PRO-006].
 * - CA-PRO-08: aprobar formaliza el protocolo; observar lo deja OBSERVADO.
 * - CA-PRO-09: los errores del backend se muestran con su código [HTTP-500].
 */
class ProtocoloViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vigente = Instrumento("INST-1", "Megger", "Megger", "MIT525", "SN-1", "2099-12-31")
    private val vencido = Instrumento("INST-2", "Megger", "Megger", "MIT1025", "SN-2", "2020-01-01")

    private fun lectura(campo: String, valor: Double) = Medicion(campo = campo, valorNumerico = valor, unidad = "GΩ")
    private val aislacion = Prueba(id = "PRU-1", protocoloId = "PROT-1", equipoCodigo = "TR-01", tipoPrueba = "Resistencia de aislación",
        instrumento = "MIT525", mediciones = listOf(
            lectura("Lectura 1 min (R1min)", 4.0), lectura("Lectura 10 min (R10min)", 10.0),
            Medicion(campo = "Índice de Polarización (IP)", valorNumerico = 2.5, unidad = "ratio")))
    private val ttr = Prueba(id = "PRU-2", protocoloId = "PROT-1", equipoCodigo = "TR-01", tipoPrueba = "Relación de transformación (TTR)",
        resultado = ResultadoPrueba.APROBADO, mediciones = listOf(lectura("Relación Teórica (Placa)", 20.0)))
    private val protocolo = Protocolo(id = "PROT-1", equipoCodigo = "TR-01", tipoEquipo = "Transformador",
        fechaHora = "2026-11-27 10:00", estado = EstadoProtocolo.REGISTRADO_TERRENO, pruebas = listOf(aislacion, ttr))

    private val protocolos = mockk<ProtocoloRepository>(relaxed = true) {
        every { allProtocolos } returns MutableStateFlow(emptyList())
        coEvery { getProtocoloById("PROT-1") } returns protocolo
    }
    private val pruebas = mockk<PruebaRepository>(relaxed = true) {
        every { getPruebasByProtocolo("PROT-1") } returns flowOf(listOf(aislacion, ttr))
        coEvery { getUltimaPruebaPorTipo(any(), any()) } returns null
    }
    private val instrumentos = mockk<InstrumentoRepository>(relaxed = true) {
        every { instrumentosActivos } returns flowOf(listOf(vigente, vencido))
    }
    private val equipos = mockk<EquipoRepository>(relaxed = true)
    // by lazy: el ViewModel usa viewModelScope al construirse y debe crearse después de MainDispatcherRule.
    private val viewModel by lazy { ProtocoloViewModel(protocolos, pruebas, instrumentos, equipos) }

    private fun editarAislacion() {
        viewModel.cargarProtocolo("PROT-1")
        viewModel.iniciarEdicionPrueba(aislacion)
    }

    private fun guardar(): String? {
        var error: String? = null
        viewModel.guardarEdicionPrueba(onSuccess = {}, onError = { error = it })
        return error
    }

    // CA-PRO-01 [PRO-004]: protocolo inexistente.
    @Test
    fun protocoloInexistente() {
        coEvery { protocolos.getProtocoloById("NO") } returns null
        viewModel.cargarProtocolo("NO")
        assertCodigo(CodigoError.PRO_004, viewModel.remoteError.value)
    }

    // CA-PRO-01 [PRO-005]: prueba inexistente.
    @Test
    fun pruebaInexistente() {
        coEvery { pruebas.getPruebaById("NO") } returns null
        viewModel.iniciarEdicionPrueba("NO")
        assertCodigo(CodigoError.PRO_005, viewModel.remoteError.value)
    }

    // CA-PRO-02 [PRO-002]: guardar sin haber iniciado la edición.
    @Test
    fun guardarSinPruebaEnEdicion() {
        assertCodigo(CodigoError.PRO_002, guardar())
    }

    // CA-PRO-02 [PRO-003]: guardar sin protocolo cargado.
    @Test
    fun guardarSinProtocolo() {
        viewModel.iniciarEdicionPrueba(aislacion)
        assertCodigo(CodigoError.PRO_003, guardar())
    }

    // CA-PRO-03 [PRO-001]: no realizada sin motivo no se guarda.
    @Test
    fun noRealizadaSinMotivo() {
        editarAislacion()
        viewModel.onEstadoPruebaChanged(EstadoPrueba.NO_REALIZADA)
        assertEquals(ResultadoPrueba.NO_REALIZADA, viewModel.edicionPruebaState.value?.resultadoCalculado)
        assertCodigo(CodigoError.PRO_001, guardar())
        coVerify(exactly = 0) { pruebas.insertPrueba(any()) }
    }

    // CA-PRO-03: no realizada con motivo se guarda.
    @Test
    fun noRealizadaConMotivo() {
        editarAislacion()
        viewModel.onEstadoPruebaChanged(EstadoPrueba.NO_REALIZADA)
        viewModel.onMotivoNoRealizadaChanged("Equipo energizado")
        assertNull(guardar())
        coVerify { pruebas.insertPrueba(match { it.motivoNoRealizada == "Equipo energizado" }) }
    }

    // CA-PRO-04: bajar la lectura de 10 min recalcula el IP y rechaza la prueba.
    @Test
    fun ipSeRecalculaAlCambiarLecturas() {
        editarAislacion()
        viewModel.actualizarMedicion(1, 5.0, "GΩ", MomentoMedicion.PREVIA)
        val estado = viewModel.edicionPruebaState.value!!
        assertEquals(ResultadoPrueba.RECHAZADO, estado.resultadoCalculado)
        assertEquals(1.25, estado.mediciones.first { it.campo.contains("IP") }.valorNumerico!!, 0.0)
    }

    // CA-PRO-05: al guardar, el resultado global del protocolo es el peor de sus pruebas.
    @Test
    fun guardarRecalculaResultadoGlobal() {
        editarAislacion()
        viewModel.actualizarMedicion(1, 5.0, "GΩ", MomentoMedicion.PREVIA)
        assertNull(guardar())
        assertEquals(ResultadoPrueba.RECHAZADO, viewModel.selectedProtocolo.value?.resultadoGlobal)
        assertNull(viewModel.edicionPruebaState.value)
    }

    // CA-PRO-06: un instrumento vencido muestra advertencia; uno vigente no.
    @Test
    fun advertenciaDeCalibracionVencida() {
        editarAislacion()
        assertFalse(viewModel.edicionPruebaState.value!!.advertenciaCalibracionVencida)
        viewModel.onInstrumentoSeleccionado(vencido)
        assertTrue(viewModel.edicionPruebaState.value!!.advertenciaCalibracionVencida)
    }

    // CA-PRO-07 [PRO-006]: un protocolo formalizado no se puede editar.
    @Test
    fun noSeEditaUnProtocoloFormalizado() {
        coEvery { protocolos.getProtocoloById("PROT-1") } returns protocolo.copy(estado = EstadoProtocolo.FORMALIZADO)
        editarAislacion()
        assertCodigo(CodigoError.PRO_006, guardar())
    }

    // CA-PRO-07 [PRO-006]: un protocolo formalizado no se formaliza de nuevo.
    @Test
    fun noSeFormalizaDosVeces() {
        coEvery { protocolos.getProtocoloById("PROT-1") } returns protocolo.copy(estado = EstadoProtocolo.FORMALIZADO)
        viewModel.cargarProtocolo("PROT-1")
        viewModel.formalizarProtocolo("Jefe", esAprobacion = true) {}
        assertCodigo(CodigoError.PRO_006, viewModel.remoteError.value)
        coVerify(exactly = 0) { protocolos.formalizarProtocolo(any(), any(), any()) }
    }

    // CA-PRO-08: aprobar formaliza; observar deja el protocolo OBSERVADO.
    @Test
    fun formalizarAprobandoYObservando() {
        viewModel.cargarProtocolo("PROT-1")
        viewModel.formalizarProtocolo("Jefe", esAprobacion = true) {}
        coVerify { protocolos.formalizarProtocolo("PROT-1", "Jefe", EstadoProtocolo.FORMALIZADO) }
        viewModel.formalizarProtocolo("Jefe", esAprobacion = false) {}
        coVerify { protocolos.formalizarProtocolo("PROT-1", "Jefe", EstadoProtocolo.OBSERVADO) }
    }

    // CA-PRO-02 [PRO-003]: formalizar sin protocolo cargado.
    @Test
    fun formalizarSinProtocolo() {
        viewModel.formalizarProtocolo("Jefe", esAprobacion = true) {}
        assertCodigo(CodigoError.PRO_003, viewModel.remoteError.value)
    }

    // CA-PRO-09 [HTTP-500]: el backend falla al guardar la prueba.
    @Test
    fun errorDelBackendAlGuardar() {
        coEvery { pruebas.insertPrueba(any()) } throws httpError(500, "INTERNAL_ERROR", "Unexpected")
        editarAislacion()
        assertNull(guardar())
        assertHttp(500, viewModel.remoteError.value)
    }
}
