package com.induztek.app.presentation.screen.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.repository.AuthRepository
import com.induztek.app.domain.repository.PruebaRepository
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

data class AdminDashboardUiState(
    val pruebas: List<Prueba> = emptyList(),
    val pruebasFiltradas: List<Prueba> = emptyList(),
    val filtroEstado: EstadoPrueba? = null,
    val isLoading: Boolean = true,
    val totalEnTerreno: Int = 0,
    val totalSincronizadas: Int = 0,
    val totalFormalizadas: Int = 0,
    val mensajeExito: String? = null,
    val error: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val pruebaRepository: PruebaRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        cargarPruebas()
    }

    private fun cargarPruebas() {
        pruebaRepository.getAll()
            .onEach { lista ->
                _uiState.update { state ->
                    val enTerreno = lista.count { it.estado == EstadoPrueba.REGISTRADO_TERRENO }
                    val sinc = lista.count { it.estado == EstadoPrueba.SINCRONIZADO || it.estado == EstadoPrueba.PENDIENTE_SYNC }
                    val formal = lista.count { it.estado == EstadoPrueba.FORMALIZADO }

                    state.copy(
                        pruebas = lista,
                        pruebasFiltradas = aplicarFiltro(lista, state.filtroEstado),
                        totalEnTerreno = enTerreno,
                        totalSincronizadas = sinc,
                        totalFormalizadas = formal,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
            .launchIn(viewModelScope)
    }

    fun onFiltrarPorEstado(estado: EstadoPrueba?) {
        _uiState.update { state ->
            state.copy(
                filtroEstado = estado,
                pruebasFiltradas = aplicarFiltro(state.pruebas, estado)
            )
        }
    }

    /**
     * Formaliza el protocolo técnico (cambia estado a FORMALIZADO).
     * Resuelve el problema del caso: certificación oficial del trabajo realizado.
     */
    fun formalizarPrueba(pruebaId: Long) {
        viewModelScope.launch {
            try {
                pruebaRepository.updateEstado(pruebaId, EstadoPrueba.FORMALIZADO)
                _uiState.update { it.copy(mensajeExito = "Protocolo oficial formalizado exitosamente.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al formalizar: ${e.localizedMessage}") }
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeExito = null, error = null) }
    }

    private fun aplicarFiltro(lista: List<Prueba>, estado: EstadoPrueba?): List<Prueba> {
        return if (estado == null) lista else lista.filter { it.estado == estado }
    }
}
