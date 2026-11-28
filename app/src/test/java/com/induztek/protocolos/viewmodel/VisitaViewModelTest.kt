package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.ActaRepository
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.data.repository.VisitaRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.model.Visita
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Visitas técnicas, protocolos generados en terreno y cierre con acta (VisitaViewModel).
 *
 * Criterios de aceptación:
 * - CA-VIS-01: una visita exige OT [VIS-001], cliente [VIS-002] e ingeniero a cargo [VIS-003].
 * - CA-VIS-02: una visita válida se crea abierta, pendiente de sincronizar, y queda seleccionada.
 * - CA-VIS-03: agregar un equipo sin visita seleccionada informa el problema [VIS-005].
 * - CA-VIS-04: un equipo solo puede tener un protocolo por visita (RN-02) [VIS-004].
 * - CA-VIS-05: al agregar un equipo se genera un protocolo con todas las pruebas requeridas para su tipo.
 * - CA-VIS-06: el cierre exige quién recibe el acta [VIS-006] y al menos un protocolo [VIS-007].
 * - CA-VIS-07: no se cierra si una prueba está incompleta [VIS-008 + REG-001/REG-002].
 * - CA-VIS-08: el cierre correcto genera el acta ACTA-<OT>, cierra la visita y resume las pruebas no realizadas.
 * - CA-VIS-09: una visita o acta inexistente se informa [VIS-009, VIS-010].
 * - CA-VIS-10: los errores del backend al cargar se muestran con su código [HTTP-503].
 */
class VisitaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val visita = Visita(id = "VIS-1", ordenTrabajo = "OT-2026-085", fecha = "2026-11-27", horaInicio = "09:00",
        cliente = "Planta Lampa", instalacion = "Subestación Norte", ingenieroACargo = "Carlos", tecnicos = "Juan")
    private val equipo = Equipo(codigo = "TR-01", tipo = TipoEquipo.TRANSFORMADOR.nombre, cliente = "Planta Lampa", ubicacion = "Norte")
    private val protocolosDeLaVisita = MutableStateFlow<List<Protocolo>>(emptyList())

    private val visitas = mockk<VisitaRepository>(relaxed = true) {
        every { allVisitas } returns MutableStateFlow(emptyList())
        coEvery { getVisitaById("VIS-1") } returns visita
    }
    private val protocolos = mockk<ProtocoloRepository>(relaxed = true) {
        every { getProtocolosByVisita(any()) } returns protocolosDeLaVisita
        coEvery { existeProtocoloParaEquipoEnVisita(any(), any()) } returns false
    }
    private val pruebas = mockk<PruebaRepository>(relaxed = true)
    private val equipos = mockk<EquipoRepository>(relaxed = true)
    private val actas = mockk<ActaRepository>(relaxed = true)
    // by lazy: el ViewModel usa viewModelScope al construirse y debe crearse después de MainDispatcherRule.
    private val viewModel by lazy { VisitaViewModel(visitas, protocolos, pruebas, equipos, actas) }

    private fun protocoloCon(prueba: Prueba) = Protocolo(codigoProtocolo = "PT-2026-0001", visitaId = "VIS-1",
        equipoCodigo = "TR-01", tipoEquipo = "Transformador", fechaHora = "2026-11-27 10:00", pruebas = listOf(prueba))

    private val medicion = Medicion(campo = "R1", valorNumerico = 1.0, unidad = "GΩ")

    // CA-VIS-01 [VIS-001]: la OT es obligatoria.
    @Test
    fun visitaSinOt() {
        viewModel.onOrdenTrabajoChanged("")
        viewModel.crearVisita {}
        assertCodigo(CodigoError.VIS_001, viewModel.nuevaVisitaState.value.errorValidacion)
    }

    // CA-VIS-01 [VIS-002]: el cliente es obligatorio.
    @Test
    fun visitaSinCliente() {
        viewModel.onClienteChanged(" ")
        viewModel.crearVisita {}
        assertCodigo(CodigoError.VIS_002, viewModel.nuevaVisitaState.value.errorValidacion)
    }

    // CA-VIS-01 [VIS-003]: el ingeniero a cargo es obligatorio.
    @Test
    fun visitaSinIngeniero() {
        viewModel.onIngenieroACargoChanged("")
        viewModel.crearVisita {}
        assertCodigo(CodigoError.VIS_003, viewModel.nuevaVisitaState.value.errorValidacion)
        coVerify(exactly = 0) { visitas.insertVisita(any()) }
    }

    // CA-VIS-02: una visita válida se crea abierta y pendiente de sincronizar.
    @Test
    fun visitaValidaSeCrea() {
        val creada = slot<Visita>()
        coEvery { visitas.insertVisita(capture(creada)) } returns Unit
        coEvery { visitas.getVisitaById(any()) } answers { creada.captured }
        var idCreado: String? = null
        viewModel.crearVisita { idCreado = it }
        assertTrue(idCreado!!.startsWith("VIS-"))
        assertEquals(false, creada.captured.cerrada)
        assertEquals(idCreado, viewModel.visitaSeleccionada.value?.id)
    }

    // CA-VIS-03 [VIS-005]: no se agrega un equipo si no hay visita seleccionada.
    @Test
    fun agregarEquipoSinVisita() {
        viewModel.agregarEquipoAVisita(equipo, "Juan") {}
        assertCodigo(CodigoError.VIS_005, viewModel.mensajeError.value)
    }

    // CA-VIS-04 [VIS-004]: RN-02, el equipo ya tiene protocolo en la visita.
    @Test
    fun agregarEquipoDuplicadoEnLaVisita() {
        coEvery { protocolos.existeProtocoloParaEquipoEnVisita("VIS-1", "TR-01") } returns true
        viewModel.cargarDetalleVisita("VIS-1")
        viewModel.agregarEquipoAVisita(equipo, "Juan") {}
        assertCodigo(CodigoError.VIS_004, viewModel.mensajeError.value)
        coVerify(exactly = 0) { protocolos.insertProtocolo(any()) }
    }

    // CA-VIS-05: el protocolo generado incluye todas las pruebas requeridas para el tipo de equipo.
    @Test
    fun agregarEquipoGeneraProtocoloConSusPruebas() {
        val creado = slot<Protocolo>()
        coEvery { protocolos.insertProtocolo(capture(creado)) } returns Unit
        viewModel.cargarDetalleVisita("VIS-1")
        var idProtocolo: String? = null
        viewModel.agregarEquipoAVisita(equipo, "Juan") { idProtocolo = it }
        assertEquals(creado.captured.id, idProtocolo)
        assertEquals(TipoEquipo.TRANSFORMADOR.pruebasRequeridas, creado.captured.pruebas.map { it.tipoPrueba })
        assertEquals(EstadoProtocolo.REGISTRADO_TERRENO, creado.captured.estado)
        assertTrue(creado.captured.codigoProtocolo.matches(Regex("PT-2026-\\d{4}")))
    }

    // CA-VIS-06 [VIS-005]: no se cierra sin visita seleccionada.
    @Test
    fun cerrarSinVisita() {
        var error: String? = null
        viewModel.cerrarVisitaYGenerarActa("Pedro", {}, { error = it })
        assertCodigo(CodigoError.VIS_005, error)
    }

    // CA-VIS-06 [VIS-006]: hay que indicar quién recibe el acta.
    @Test
    fun cerrarSinReceptor() {
        viewModel.cargarDetalleVisita("VIS-1")
        var error: String? = null
        viewModel.cerrarVisitaYGenerarActa(" ", {}, { error = it })
        assertCodigo(CodigoError.VIS_006, error)
    }

    // CA-VIS-06 [VIS-007]: no se cierra una visita sin protocolos.
    @Test
    fun cerrarSinProtocolos() {
        viewModel.cargarDetalleVisita("VIS-1")
        var error: String? = null
        viewModel.cerrarVisitaYGenerarActa("Pedro", {}, { error = it })
        assertCodigo(CodigoError.VIS_007, error)
    }

    // CA-VIS-07 [VIS-008 + REG-001]: una prueba no realizada sin motivo bloquea el cierre.
    @Test
    fun cerrarConPruebaIncompleta() {
        protocolosDeLaVisita.value = listOf(protocoloCon(Prueba(tipoPrueba = "TTR", estadoPrueba = EstadoPrueba.NO_REALIZADA)))
        viewModel.cargarDetalleVisita("VIS-1")
        var error: String? = null
        viewModel.cerrarVisitaYGenerarActa("Pedro", {}, { error = it })
        assertCodigo(CodigoError.VIS_008, error)
        assertTrue(error!!.contains("[REG-001]"))
        coVerify(exactly = 0) { actas.insertActa(any()) }
    }

    // CA-VIS-08: el cierre correcto genera el acta y cierra la visita.
    @Test
    fun cerrarVisitaGeneraActa() {
        protocolosDeLaVisita.value = listOf(
            protocoloCon(Prueba(tipoPrueba = "Aislación", mediciones = listOf(medicion))).copy(
                pruebas = listOf(
                    Prueba(tipoPrueba = "Aislación", mediciones = listOf(medicion)),
                    Prueba(tipoPrueba = "TTR", estadoPrueba = EstadoPrueba.NO_REALIZADA, motivoNoRealizada = "Equipo energizado")
                )
            )
        )
        viewModel.cargarDetalleVisita("VIS-1")
        var acta: Acta? = null
        viewModel.cerrarVisitaYGenerarActa(" Pedro ", { acta = it }, { error("no debía fallar: $it") })
        assertNotNull(acta)
        assertEquals("ACTA-2026-085", acta!!.id)
        assertEquals("Pedro", acta!!.recibidoPor)
        assertTrue(acta!!.resumenProtocolos.contains("NO REALIZADA: TTR (Motivo: Equipo energizado)"))
        coVerify { visitas.cerrarVisita("VIS-1") }
    }

    // CA-VIS-09 [VIS-009]: una visita inexistente se informa.
    @Test
    fun visitaInexistente() {
        coEvery { visitas.getVisitaById("NO") } returns null
        viewModel.cargarDetalleVisita("NO")
        assertCodigo(CodigoError.VIS_009, viewModel.mensajeError.value)
    }

    // CA-VIS-09 [VIS-010]: un acta inexistente se informa.
    @Test
    fun actaInexistente() {
        coEvery { actas.getActaById("NO") } returns null
        viewModel.cargarActa("NO")
        assertCodigo(CodigoError.VIS_010, viewModel.mensajeError.value)
    }

    // CA-VIS-10 [HTTP-503]: el servicio de operaciones no responde al refrescar.
    @Test
    fun refreshConServicioCaido() {
        coEvery { visitas.refresh() } throws httpError(503, "SERVICE_UNAVAILABLE", "Down")
        viewModel.refresh()
        assertHttp(503, viewModel.mensajeError.value)
    }
}
