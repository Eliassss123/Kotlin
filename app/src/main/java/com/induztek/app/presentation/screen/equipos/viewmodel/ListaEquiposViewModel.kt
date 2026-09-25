package com.induztek.app.presentation.screen.equipos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.TipoEquipo
import com.induztek.app.domain.repository.AuthRepository
import com.induztek.app.domain.repository.EquipoRepository
import com.induztek.app.presentation.screen.equipos.state.ListaEquiposUiState
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

@HiltViewModel
class ListaEquiposViewModel @Inject constructor(
    private val equipoRepository: EquipoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaEquiposUiState())
    val uiState: StateFlow<ListaEquiposUiState> = _uiState.asStateFlow()

    init {
        equipoRepository.getAll()
            .onEach { equipos ->
                _uiState.update { state ->
                    val trfCount   = equipos.count { it.tipo == TipoEquipo.TRANSFORMADOR }
                    val releCount  = equipos.count { it.tipo == TipoEquipo.RELE }
                    val celdaCount = equipos.count { it.tipo == TipoEquipo.CELDA }
                    val cableCount = equipos.count { it.tipo == TipoEquipo.CABLE }
                    val mallaCount = equipos.count { it.tipo == TipoEquipo.MALLA_TIERRA }
                    val genCount   = equipos.count { it.tipo == TipoEquipo.GENERADOR }

                    state.copy(
                        equipos                  = equipos,
                        equiposFiltrados         = filtrar(equipos, state.searchQuery, state.categoriaSeleccionada),
                        totalTransformadores     = trfCount,
                        totalReles               = releCount,
                        totalCeldas              = celdaCount,
                        totalCables              = cableCount,
                        totalMallasTierra        = mallaCount,
                        totalGeneradores         = genCount,
                        isLoading                = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery      = query,
                equiposFiltrados = filtrar(state.equipos, query, state.categoriaSeleccionada)
            )
        }
    }

    fun onCategoriaSelected(categoria: TipoEquipo?) {
        _uiState.update { state ->
            state.copy(
                categoriaSeleccionada = categoria,
                equiposFiltrados      = filtrar(state.equipos, state.searchQuery, categoria)
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    private fun filtrar(equipos: List<Equipo>, query: String, categoria: TipoEquipo?): List<Equipo> {
        return equipos.filter { equipo ->
            val coincideCategoria = (categoria == null || equipo.tipo == categoria)
            val coincideTexto = if (query.isBlank()) true else {
                equipo.codigo.contains(query, ignoreCase = true) ||
                equipo.instalacion.contains(query, ignoreCase = true) ||
                equipo.tipo.name.contains(query, ignoreCase = true) ||
                equipo.descripcion.contains(query, ignoreCase = true)
            }
            coincideCategoria && coincideTexto
        }
    }
}
