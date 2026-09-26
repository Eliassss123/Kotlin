package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NuevaPruebaState(
    val equipoCodigo: String = "",
    val tipoPrueba: String = "Resistencia de Aislamiento (Megger)",
    val ubicacion: String = "",
    val fechaHora: String = "",
    val mediciones: List<Medicion> = listOf(
        Medicion("Resistencia Alta-Tierra", "4.2 GΩ"),
        Medicion("Índice de Polarización", "1.75")
    ),
    val estado: EstadoProtocolo = EstadoProtocolo.REGISTRADO_TERRENO,
    val observaciones: String = "",
    val tecnico: String = "Elias Farías (el.farr@duocuc.cl)"
)

class PruebaViewModel(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository,
    private val protocoloRepository: ProtocoloRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NuevaPruebaState())
    val uiState: StateFlow<NuevaPruebaState> = _uiState.asStateFlow()

    private val _equiposDisponibles = MutableStateFlow<List<Equipo>>(emptyList())
    val equiposDisponibles: StateFlow<List<Equipo>> = _equiposDisponibles.asStateFlow()

    init {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        _uiState.value = _uiState.value.copy(fechaHora = formatter.format(Date()))

        viewModelScope.launch {
            equipoRepository.allEquipos.collect { list ->
                _equiposDisponibles.value = list
                if (list.isNotEmpty() && _uiState.value.equipoCodigo.isBlank()) {
                    setEquipo(list.first())
                }
            }
        }
    }

    fun setEquipo(equipo: Equipo) {
        _uiState.value = _uiState.value.copy(
            equipoCodigo = equipo.codigo,
            ubicacion = equipo.ubicacion
        )
    }

    fun onEquipoCodigoChanged(codigo: String) {
        _uiState.value = _uiState.value.copy(equipoCodigo = codigo)
        viewModelScope.launch {
            val eq = equipoRepository.getEquipoByCodigo(codigo)
            if (eq != null) {
                _uiState.value = _uiState.value.copy(ubicacion = eq.ubicacion)
            }
        }
    }

    fun onTipoPruebaChanged(tipo: String) {
        _uiState.value = _uiState.value.copy(tipoPrueba = tipo)
    }

    fun onUbicacionChanged(ubicacion: String) {
        _uiState.value = _uiState.value.copy(ubicacion = ubicacion)
    }

    fun onFechaHoraChanged(fechaHora: String) {
        _uiState.value = _uiState.value.copy(fechaHora = fechaHora)
    }

    fun onEstadoChanged(estado: EstadoProtocolo) {
        _uiState.value = _uiState.value.copy(estado = estado)
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.value = _uiState.value.copy(observaciones = obs)
    }

    fun agregarMedicion(campo: String, valor: String) {
        if (campo.isNotBlank() && valor.isNotBlank()) {
            val listaActual = _uiState.value.mediciones.toMutableList()
            listaActual.add(Medicion(campo.trim(), valor.trim()))
            _uiState.value = _uiState.value.copy(mediciones = listaActual)
        }
    }

    fun eliminarMedicion(index: Int) {
        val listaActual = _uiState.value.mediciones.toMutableList()
        if (index in listaActual.indices) {
            listaActual.removeAt(index)
            _uiState.value = _uiState.value.copy(mediciones = listaActual)
        }
    }

    fun guardarProtocolo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val timestamp = System.currentTimeMillis()
            val idPrueba = "PRU-$timestamp"
            val idProtocolo = "PROT-$timestamp"
            val codigoProtocolo = "IND-PROT-${state.equipoCodigo.take(5)}-$timestamp"

            val nuevaPrueba = Prueba(
                id = idPrueba,
                equipoCodigo = state.equipoCodigo,
                tipoPrueba = state.tipoPrueba,
                ubicacion = state.ubicacion,
                fechaHora = state.fechaHora,
                mediciones = state.mediciones,
                estado = state.estado,
                observaciones = state.observaciones
            )

            val nuevoProtocolo = Protocolo(
                id = idProtocolo,
                codigoProtocolo = codigoProtocolo,
                equipoCodigo = state.equipoCodigo,
                tipoEquipo = state.tipoPrueba,
                fechaHora = state.fechaHora,
                estado = state.estado,
                tecnico = state.tecnico,
                observaciones = state.observaciones,
                mediciones = state.mediciones
            )

            pruebaRepository.insertPrueba(nuevaPrueba)
            protocoloRepository.insertProtocolo(nuevoProtocolo)

            // Actualizar ultimo estado del equipo si existe
            val equipo = equipoRepository.getEquipoByCodigo(state.equipoCodigo)
            if (equipo != null) {
                equipoRepository.insertEquipo(equipo.copy(ultimoEstado = state.estado))
            }

            onSuccess()
        }
    }
}
