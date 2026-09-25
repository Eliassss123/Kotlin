package com.induztek.app.presentation.screen.historial.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.repository.EquipoRepository
import com.induztek.app.domain.repository.PruebaRepository
import com.induztek.app.presentation.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistorialEquipoUiState(
    val equipo: Equipo? = null,
    val historialPruebas: List<Prueba> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistorialEquipoViewModel @Inject constructor(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialEquipoUiState())
    val uiState: StateFlow<HistorialEquipoUiState> = _uiState.asStateFlow()

    init {
        val equipoId = savedStateHandle.get<Long>(AppDestination.HistorialEquipo.ARG_EQUIPO_ID)
        if (equipoId != null) {
            cargarDatos(equipoId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Equipo no identificado") }
        }
    }

    private fun cargarDatos(equipoId: Long) {
        viewModelScope.launch {
            val eq = equipoRepository.getById(equipoId)
            _uiState.update { it.copy(equipo = eq) }
        }

        pruebaRepository.getByEquipo(equipoId)
            .onEach { pruebas ->
                _uiState.update { it.copy(historialPruebas = pruebas, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
            .launchIn(viewModelScope)
    }
}
