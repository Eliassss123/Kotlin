package com.induztek.app.presentation.screen.nuevaprueba.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.model.TipoEquipo
import com.induztek.app.domain.repository.EquipoRepository
import com.induztek.app.domain.repository.PruebaRepository
import com.induztek.app.presentation.navigation.AppDestination
import com.induztek.app.presentation.screen.nuevaprueba.state.NuevaPruebaEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NuevaPruebaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: NuevaPruebaViewModel
    private lateinit var fakePruebaRepo: FakePruebaRepository
    private lateinit var fakeEquipoRepo: FakeEquipoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePruebaRepo = FakePruebaRepository()
        fakeEquipoRepo = FakeEquipoRepository()

        val savedStateHandle = SavedStateHandle(mapOf(AppDestination.NuevaPrueba.ARG_EQUIPO_ID to 1L))
        viewModel = NuevaPruebaViewModel(fakePruebaRepo, fakeEquipoRepo, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `estado inicial tiene fecha y hora pre-poblados`() {
        val state = viewModel.uiState.value
        assertTrue("fecha debe tener formato YYYY-MM-DD", state.fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        assertTrue("hora debe tener formato HH:mm", state.hora.matches(Regex("\\d{2}:\\d{2}")))
        assertEquals(1L, state.equipoId)
    }

    @Test
    fun `guardarPrueba sin mediciones muestra error y no guarda`() {
        viewModel.onTecnicoChanged("Ing. Carlos Mendoza")
        viewModel.guardarPrueba()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(fakePruebaRepo.pruebas.isEmpty())
    }

    @Test
    fun `guardarPrueba exitoso emite NavigateToDetalle y persiste en estado REGISTRADO_TERRENO`() = runTest {
        viewModel.onTecnicoChanged("Ing. Carlos Mendoza")
        viewModel.onMedicionChanged("resistencia", "1250 MΩ")

        var effectRecibido: NuevaPruebaEffect? = null
        val job = launch {
            viewModel.effects.collect { effectRecibido = it }
        }

        viewModel.guardarPrueba()
        advanceUntilIdle()

        assertTrue(effectRecibido is NuevaPruebaEffect.NavigateToDetalle)
        assertEquals(EstadoPrueba.REGISTRADO_TERRENO, fakePruebaRepo.pruebas.first().estado)
        job.cancel()
    }

    @Test
    fun `onFotoCaptured actualiza fotoUriPath en el estado`() {
        val rutaFoto = "/data/data/com.induztek.app/files/images/foto_evidencia.jpg"
        viewModel.onFotoCaptured(rutaFoto)
        assertEquals(rutaFoto, viewModel.uiState.value.fotoUriPath)
    }

    // ── Fakes ─────────────────────────────────────────────────────────────

    class FakePruebaRepository : PruebaRepository {
        val pruebas = mutableListOf<Prueba>()
        private var nextId = 1L

        override fun getAll(): Flow<List<Prueba>> = emptyFlow()
        override fun getByEquipo(equipoId: Long): Flow<List<Prueba>> = emptyFlow()
        override fun getPendienteSync(): Flow<List<Prueba>> = emptyFlow()
        override suspend fun getById(id: Long): Prueba? = pruebas.find { it.id == id }

        override suspend fun insert(prueba: Prueba): Long {
            val id = nextId++
            pruebas.add(prueba.copy(id = id))
            return id
        }

        override suspend fun update(prueba: Prueba) {
            val idx = pruebas.indexOfFirst { it.id == prueba.id }
            if (idx >= 0) pruebas[idx] = prueba
        }

        override suspend fun updateEstado(id: Long, nuevoEstado: EstadoPrueba) {
            val idx = pruebas.indexOfFirst { it.id == id }
            if (idx >= 0) pruebas[idx] = pruebas[idx].copy(estado = nuevoEstado)
        }

        override suspend fun delete(prueba: Prueba) {
            pruebas.removeIf { it.id == prueba.id }
        }
    }

    class FakeEquipoRepository : EquipoRepository {
        override fun getAll(): Flow<List<Equipo>> = emptyFlow()
        override suspend fun getById(id: Long): Equipo = Equipo(
            id = id,
            codigo = "TRF-001",
            tipo = TipoEquipo.TRANSFORMADOR,
            instalacion = "Subestación Norte"
        )
        override suspend fun insert(equipo: Equipo): Long = 1L
        override suspend fun update(equipo: Equipo) {}
        override suspend fun delete(equipo: Equipo) {}
    }
}
