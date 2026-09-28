// ============================================================================
// ARCHIVO : viewmodel/ProtocoloViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : estado de las pantallas de protocolos guardados (lista y detalle). Sin lógica compleja: solo expone
//           datos.
// ============================================================================

package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.model.Protocolo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Recibe el repositorio de protocolos.
class ProtocoloViewModel(
    private val protocoloRepository: ProtocoloRepository
) : ViewModel() {

    // Al crearse, siembra los protocolos de ejemplo si no hay ninguno.
    init {
        viewModelScope.launch {
            protocoloRepository.seedInitialDataIfEmpty()
        }
    }

    // Lista de protocolos como StateFlow (la pantalla la observa). stateIn / WhileSubscribed: ver EquipoViewModel.
    val protocolos: StateFlow<List<Protocolo>> = protocoloRepository.allProtocolos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Protocolo abierto en el detalle (null mientras carga).
    private val _selectedProtocolo = MutableStateFlow<Protocolo?>(null)
    val selectedProtocolo: StateFlow<Protocolo?> = _selectedProtocolo.asStateFlow()

    // Se llama desde la pantalla de detalle con el id que llegó por la ruta de navegación.
    fun cargarProtocolo(id: String) {
        viewModelScope.launch {
            _selectedProtocolo.value = protocoloRepository.getProtocoloById(id)
        }
    }
}
