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

class ProtocoloViewModel(
    private val protocoloRepository: ProtocoloRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            protocoloRepository.seedInitialDataIfEmpty()
        }
    }

    val protocolos: StateFlow<List<Protocolo>> = protocoloRepository.allProtocolos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedProtocolo = MutableStateFlow<Protocolo?>(null)
    val selectedProtocolo: StateFlow<Protocolo?> = _selectedProtocolo.asStateFlow()

    fun cargarProtocolo(id: String) {
        viewModelScope.launch {
            _selectedProtocolo.value = protocoloRepository.getProtocoloById(id)
        }
    }
}
