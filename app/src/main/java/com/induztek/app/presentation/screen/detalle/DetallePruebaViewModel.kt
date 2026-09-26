package com.induztek.app.presentation.screen.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.repository.PruebaRepository
import com.induztek.app.presentation.navigation.AppDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetallePruebaUiState(
    val prueba: Prueba?       = null,
    val isLoading: Boolean    = true,
    val errorMessage: String? = null
)

/**
 * ViewModel del detalle de prueba.
 *
 * Lee pruebaId del SavedStateHandle (inyectado automáticamente por
 * Navigation Compose cuando se declara el navArgument en el NavGraph).
 *
 * Usamos if/else en lugar de ?: run { return@init } porque los bloques
 * init de Kotlin no soportan return con label.
 */
@HiltViewModel
class DetallePruebaViewModel @Inject constructor(
    private val pruebaRepository: PruebaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetallePruebaUiState())
    val uiState: StateFlow<DetallePruebaUiState> = _uiState.asStateFlow()

    init {
        val pruebaId = savedStateHandle.get<Long>(AppDestination.DetallePrueba.ARG_PRUEBA_ID)

        if (pruebaId == null) {
            // ID no encontrado — mostrar error sin crashear
            _uiState.update { it.copy(isLoading = false, errorMessage = "ID de prueba no encontrado") }
        } else {
            // pruebaId es Long (no nullable) aquí — smart cast garantizado
            viewModelScope.launch {
                val prueba = pruebaRepository.getById(pruebaId)
                _uiState.update { it.copy(prueba = prueba, isLoading = false) }
            }
        }
    }
}
