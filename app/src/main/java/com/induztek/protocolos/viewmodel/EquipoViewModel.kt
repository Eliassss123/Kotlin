// ============================================================================
// ARCHIVO : viewmodel/EquipoViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : el "cerebro" de las pantallas de equipos. Guarda el ESTADO (lista, texto de búsqueda, equipo abierto)
//           y la LÓGICA (filtrar). Sobrevive a rotaciones de pantalla. La pantalla solo observa y avisa eventos.
// ============================================================================

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

// ViewModel: clase de Android para guardar estado de pantalla. Recibe los repositorios por constructor.
class EquipoViewModel(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository
) : ViewModel() {

    // Patrón _privado / público: '_searchQuery' es MutableStateFlow (se puede modificar, pero solo aquí dentro).
    // 'searchQuery' (abajo) es StateFlow de solo lectura para la pantalla.
    // StateFlow = valor OBSERVABLE: cuando cambia, Compose se entera y redibuja.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // init = se ejecuta al crear el ViewModel. Lanza una corrutina (viewModelScope.launch) que siembra los datos de
    //   ejemplo sin bloquear la pantalla.
    init {
        viewModelScope.launch {
            equipoRepository.seedInitialDataIfEmpty()
            pruebaRepository.seedInitialDataIfEmpty()
        }
    }

    // LÓGICA DE NEGOCIO: combine(...) junta dos flujos (lista de equipos de la BD + texto de búsqueda).
    // Cada vez que cambia cualquiera de los dos, se recalcula. Si el texto está vacío muestra todo; si no,
    // filtra por código, tipo o ubicación (ignoreCase = ignorar mayúsculas).
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
    // stateIn convierte el flujo en StateFlow.
    // WhileSubscribed(5000) = sigue activo 5 s después de que la pantalla deje de observar (evita recalcular al
    //   rotar).
    // initialValue = lista vacía mientras carga.
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // EVENTO de la UI: el usuario escribió en el buscador -> guardamos el texto. El flujo 'equipos' se recalcula
    //   solo.
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    // Estado del equipo abierto en la pantalla de detalle (null = todavía cargando).
    private val _selectedEquipo = MutableStateFlow<Equipo?>(null)
    val selectedEquipo: StateFlow<Equipo?> = _selectedEquipo.asStateFlow()

    // Pruebas anteriores del equipo abierto.
    private val _historialPruebas = MutableStateFlow<List<Prueba>>(emptyList())
    val historialPruebas: StateFlow<List<Prueba>> = _historialPruebas.asStateFlow()

    // Busca el equipo por código y luego se suscribe (collect) al historial de pruebas de ese equipo.
    // Para aprender: cada llamada crea una nueva suscripción y las anteriores no se cancelan. En una app más grande
    // se guardaría el Job para cancelarlo al cambiar de equipo.
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
