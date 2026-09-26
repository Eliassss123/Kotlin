package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquipoViewModel(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            equipoRepository.seedInitialDataIfEmpty()
            pruebaRepository.seedInitialDataIfEmpty()
        }
    }

    val equipos: StateFlow<List<Equipo>> = combine(
        equipoRepository.allEquipos,
        _searchQuery
    ) { lista, query ->
        if (query.isBlank()) {
            lista
        } else {
            lista.filter {
                it.codigo.contains(query, ignoreCase = true) ||
                        it.tipo.contains(query, ignoreCase = true) ||
                        it.ubicacion.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private val _selectedEquipo = MutableStateFlow<Equipo?>(null)
    val selectedEquipo: StateFlow<Equipo?> = _selectedEquipo.asStateFlow()

    private val _historialPruebas = MutableStateFlow<List<Prueba>>(emptyList())
    val historialPruebas: StateFlow<List<Prueba>> = _historialPruebas.asStateFlow()

    fun cargarDetalleEquipo(codigo: String) {
        viewModelScope.launch {
            val equipo = equipoRepository.getEquipoByCodigo(codigo)
            _selectedEquipo.value = equipo
            if (equipo != null) {
                pruebaRepository.getPruebasByEquipo(equipo.codigo).collect { pruebas ->
                    _historialPruebas.value = pruebas
                }
            }
        }
    }
}
