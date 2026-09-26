package com.induztek.app.presentation.screen.nuevaprueba.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.model.TipoEquipo
import com.induztek.app.domain.repository.EquipoRepository
import com.induztek.app.domain.repository.PruebaRepository
import com.induztek.app.presentation.navigation.AppDestination
import com.induztek.app.presentation.screen.nuevaprueba.state.NuevaPruebaEffect
import com.induztek.app.presentation.screen.nuevaprueba.state.NuevaPruebaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NuevaPruebaViewModel @Inject constructor(
    private val pruebaRepository: PruebaRepository,
    private val equipoRepository: EquipoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NuevaPruebaUiState(
            fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            hora  = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        )
    )
    val uiState: StateFlow<NuevaPruebaUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<NuevaPruebaEffect>()
    val effects: SharedFlow<NuevaPruebaEffect> = _effects.asSharedFlow()

    init {
        val equipoId = savedStateHandle.get<Long>(AppDestination.NuevaPrueba.ARG_EQUIPO_ID)

        if (equipoId == null) {
            _uiState.update { it.copy(errorMessage = "Error: equipo no identificado") }
        } else {
            _uiState.update { it.copy(equipoId = equipoId) }

            viewModelScope.launch {
                val equipo = equipoRepository.getById(equipoId)
                if (equipo != null) {
                    val nombre = "${equipo.codigo} — ${equipo.instalacion}"
                    _uiState.update { it.copy(nombreEquipo = nombre, tipoEquipo = equipo.tipo) }
                }
            }
        }
    }

    fun onTecnicoChanged(value: String) {
        _uiState.update { it.copy(tecnico = value) }
    }

    fun onFechaChanged(value: String) {
        _uiState.update { it.copy(fecha = value) }
    }

    fun onHoraChanged(value: String) {
        _uiState.update { it.copy(hora = value) }
    }

    fun onObservacionesChanged(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    fun onMedicionChanged(clave: String, valor: String) {
        _uiState.update { state ->
            state.copy(mediciones = state.mediciones + (clave to valor))
        }
    }

    fun onFotoCaptured(absolutePath: String) {
        _uiState.update { it.copy(fotoUriPath = absolutePath) }
    }

    fun onFotoRemoved() {
        _uiState.update { it.copy(fotoUriPath = null) }
    }

    fun onCameraPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(cameraPermissionGranted = granted) }
        if (!granted) {
            viewModelScope.launch {
                _effects.emit(
                    NuevaPruebaEffect.ShowSnackbar("Permiso de cámara denegado. Habilítalo en los Ajustes del dispositivo.")
                )
            }
        }
    }

    fun guardarPrueba() {
        val state = _uiState.value

        val error = when {
            state.equipoId == null     -> "Error: equipo no identificado"
            state.tecnico.isBlank()    -> "Ingresa el nombre del técnico responsable"
            state.fecha.isBlank()      -> "La fecha es requerida"
            state.hora.isBlank()       -> "La hora es requerida"
            state.mediciones.isEmpty() -> "Registra al menos un valor de medición"
            else                       -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Formatear mediciones agregando sus unidades técnicas según la prueba
                val medicionesConUnidades = state.mediciones.mapValues { (clave, valor) ->
                    when (clave) {
                        "resistencia_aislamiento" -> if (valor.endsWith("MΩ")) valor else "$valor MΩ"
                        "tension_prueba"          -> if (valor.endsWith("kV")) valor else "$valor kV"
                        "temperatura"             -> if (valor.endsWith("°C")) valor else "$valor °C"
                        "tiempo_disparo"          -> if (valor.endsWith("ms")) valor else "$valor ms"
                        "corriente_pickup"        -> if (valor.endsWith("A")) valor else "$valor A"
                        "tension_control"         -> if (valor.endsWith("V")) valor else "$valor V"
                        "resistencia_contactos"   -> if (valor.endsWith("µΩ")) valor else "$valor µΩ"
                        "resistencia_tierra"      -> if (valor.endsWith("Ω")) valor else "$valor Ω"
                        "tension_salida"          -> if (valor.endsWith("V")) valor else "$valor V"
                        "frecuencia"              -> if (valor.endsWith("Hz")) valor else "$valor Hz"
                        else                      -> valor
                    }
                }

                val nuevaPrueba = Prueba(
                    equipoId      = state.equipoId!!,
                    fecha         = state.fecha,
                    hora          = state.hora,
                    tecnico       = state.tecnico,
                    mediciones    = medicionesConUnidades,
                    observaciones = state.observaciones,
                    fotoUriPath   = state.fotoUriPath,
                    estado        = EstadoPrueba.REGISTRADO_TERRENO
                )
                val pruebaId = pruebaRepository.insert(nuevaPrueba)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
                _effects.emit(NuevaPruebaEffect.NavigateToDetalle(pruebaId))
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error al guardar en Room: ${e.localizedMessage}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
