package com.induztek.protocolos.viewmodel

import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.model.ClasificacionEquipo
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.testutil.MainDispatcherRule
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Registro, búsqueda y detalle de equipos (EquipoViewModel).
 *
 * Criterios de aceptación:
 * - CA-EQ-01: sin TAG se exige marca y número de serie [EQ-001].
 * - CA-EQ-02: el cliente y la ubicación son obligatorios [EQ-002, EQ-003].
 * - CA-EQ-03: el año debe estar entre 1900 y 2026 [EQ-004].
 * - CA-EQ-04: con TAG el código se guarda en mayúsculas; sin TAG se genera EQ-MARC-SERIE.
 * - CA-EQ-05: si ya existe un equipo igual en la instalación se advierte (RT-02) [EQ-005].
 * - CA-EQ-06: un código inexistente en el detalle se informa [EQ-006].
 * - CA-EQ-07: la búsqueda filtra por código, TAG, cliente, tipo, ubicación o serie; también por empresa y clasificación.
 * - CA-EQ-08: los errores del backend se muestran con su código [HTTP-409].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EquipoViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val transformador = Equipo(codigo = "TR-01", tagCliente = "TAG-TR", tipo = TipoEquipo.TRANSFORMADOR.nombre,
        cliente = "Planta Lampa", ubicacion = "Subestación Norte", numeroSerie = "SN-100")
    private val rele = Equipo(codigo = "RL-07", tipo = TipoEquipo.RELE_PROTECCION.nombre,
        cliente = "Minera Andes", ubicacion = "Sala Eléctrica", numeroSerie = "SN-200")

    private val equipos = mockk<EquipoRepository>(relaxed = true) {
        every { allEquipos } returns MutableStateFlow(listOf(transformador, rele))
        coEvery { buscarPosibleDuplicado(any(), any(), any(), any()) } returns null
    }
    private val pruebas = mockk<PruebaRepository>(relaxed = true)
    // by lazy: el ViewModel usa viewModelScope al construirse y debe crearse después de MainDispatcherRule.
    private val viewModel by lazy { EquipoViewModel(equipos, pruebas) }

    private fun formularioValido() {
        viewModel.onCodigoChanged("tr-nuevo-01")
        viewModel.onClienteNuevoChanged("Planta Lampa")
        viewModel.onUbicacionNuevaChanged("Subestación Sur")
        viewModel.onAnioChanged("2020")
    }

    // CA-EQ-01 [EQ-001]: sin TAG ni marca/serie no hay forma de identificar el equipo.
    @Test
    fun sinTagNiMarcaNiSerie() {
        viewModel.guardarEquipoNuevo {}
        assertCodigo(CodigoError.EQ_001, viewModel.nuevoEquipoState.value.errorValidacion)
    }

    // CA-EQ-02 [EQ-002]: el cliente es obligatorio.
    @Test
    fun sinCliente() {
        formularioValido()
        viewModel.onClienteNuevoChanged(" ")
        viewModel.guardarEquipoNuevo {}
        assertCodigo(CodigoError.EQ_002, viewModel.nuevoEquipoState.value.errorValidacion)
    }

    // CA-EQ-02 [EQ-003]: la ubicación es obligatoria.
    @Test
    fun sinUbicacion() {
        formularioValido()
        viewModel.onUbicacionNuevaChanged("")
        viewModel.guardarEquipoNuevo {}
        assertCodigo(CodigoError.EQ_003, viewModel.nuevoEquipoState.value.errorValidacion)
    }

    // CA-EQ-03 [EQ-004]: año fuera de rango.
    @Test
    fun anioFueraDeRango() {
        formularioValido()
        viewModel.onAnioChanged("1850")
        viewModel.guardarEquipoNuevo {}
        assertCodigo(CodigoError.EQ_004, viewModel.nuevoEquipoState.value.errorValidacion)
    }

    // CA-EQ-04: con TAG se guarda el código en mayúsculas y se limpia el formulario.
    @Test
    fun guardarConTag() {
        var guardado: Equipo? = null
        formularioValido()
        viewModel.guardarEquipoNuevo { guardado = it }
        assertEquals("TR-NUEVO-01", guardado?.codigo)
        assertEquals("", viewModel.nuevoEquipoState.value.codigo)
        coVerify { equipos.insertEquipo(match { it.codigo == "TR-NUEVO-01" }) }
    }

    // CA-EQ-04: sin TAG el código se arma con la marca y el número de serie.
    @Test
    fun guardarSinTagGeneraCodigo() {
        var guardado: Equipo? = null
        viewModel.onMarcaChanged("Siemens")
        viewModel.onNumeroSerieChanged("abc123")
        viewModel.onClienteNuevoChanged("Planta Lampa")
        viewModel.onUbicacionNuevaChanged("Subestación Sur")
        viewModel.guardarEquipoNuevo { guardado = it }
        assertEquals("EQ-SIEM-ABC123", guardado?.codigo)
    }

    // CA-EQ-05 [EQ-005]: se advierte un posible duplicado en la misma instalación.
    @Test
    fun advertenciaDePosibleDuplicado() {
        coEvery { equipos.buscarPosibleDuplicado(any(), any(), any(), any()) } returns transformador
        viewModel.onUbicacionNuevaChanged("Subestación Norte")
        viewModel.onTagClienteChanged("TAG-TR")
        assertCodigo(CodigoError.EQ_005, viewModel.nuevoEquipoState.value.advertenciaDuplicado)
    }

    // CA-EQ-05: si no hay duplicado no se muestra advertencia.
    @Test
    fun sinDuplicadoNoHayAdvertencia() {
        coEvery { equipos.buscarPosibleDuplicado(any(), any(), any(), any()) } returns null
        viewModel.onUbicacionNuevaChanged("Subestación Norte")
        viewModel.onTagClienteChanged("TAG-NUEVO")
        assertNull(viewModel.nuevoEquipoState.value.advertenciaDuplicado)
    }

    // CA-EQ-06 [EQ-006]: el detalle de un código inexistente informa el error.
    @Test
    fun detalleDeEquipoInexistente() {
        coEvery { equipos.getEquipoByCodigo("NO-EXISTE") } returns null
        viewModel.cargarDetalleEquipo("NO-EXISTE")
        assertCodigo(CodigoError.EQ_006, viewModel.remoteError.value)
        assertNull(viewModel.selectedEquipo.value)
    }

    // CA-EQ-06: el detalle de un equipo existente carga su historial de pruebas.
    @Test
    fun detalleDeEquipoExistente() {
        coEvery { equipos.getEquipoByCodigo("TR-01") } returns transformador
        every { pruebas.getPruebasByEquipo("TR-01") } returns flowOf(emptyList())
        viewModel.cargarDetalleEquipo("TR-01")
        assertEquals(transformador, viewModel.selectedEquipo.value)
        assertNull(viewModel.remoteError.value)
    }

    // CA-EQ-07: la búsqueda y los filtros reducen la lista de equipos.
    @Test
    fun busquedaYFiltros() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.equipos.collect {} }
        assertEquals(2, viewModel.equipos.value.size)

        viewModel.onSearchQueryChanged("sn-200")
        assertEquals(listOf(rele), viewModel.equipos.value)

        viewModel.onSearchQueryChanged("")
        viewModel.setEmpresaCliente("planta lampa")
        assertEquals(listOf(transformador), viewModel.equipos.value)

        viewModel.setEmpresaCliente(null)
        viewModel.setFiltroClasificacion(ClasificacionEquipo.PRIMARIO)
        assertEquals(listOf(transformador), viewModel.equipos.value)
    }

    // CA-EQ-08 [HTTP-409]: el backend rechaza un código de equipo duplicado.
    @Test
    fun guardarConCodigoDuplicadoEnBackend() {
        coEvery { equipos.insertEquipo(any()) } throws httpError(409, "CONFLICT", "Equipment code already exists")
        formularioValido()
        viewModel.guardarEquipoNuevo {}
        assertHttp(409, viewModel.remoteError.value)
    }
}
