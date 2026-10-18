// ============================================================================
// ARCHIVO : viewmodel/AdminViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : Módulo de administración para Jefe Técnico (P-03) según RT-11 y RF-22:
//           - Gestión de instrumentos y renovación de calibraciones (RN-11, RT-11)
//           - Activación y desactivación de personal técnico (RF-22)
// ============================================================================

package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.model.Instrumento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Estado de la UI para la creación de un nuevo instrumento en el módulo de administración.
 */
data class NuevoInstrumentoState(
    val tipo: String = "Medidor de Resistencia de Aislamiento",
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val vencimientoCalibracion: String = "2027-12-31",
    val errorValidacion: String? = null
)

/**
 * ViewModel que soporta las funciones administrativas del Jefe Técnico (P-03),
 * incluyendo la gestión de instrumentos (RN-11, RT-11) y la activación/desactivación de personal (RF-22).
 */
class AdminViewModel(
    private val instrumentoRepository: InstrumentoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    /** Flujo con la lista de todos los instrumentos registrados. */
    val instrumentos: StateFlow<List<Instrumento>> = instrumentoRepository.allInstrumentos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Flujo con la lista de todo el personal del sistema. */
    val personal: StateFlow<List<UsuarioEntity>> = usuarioRepository.allUsuarios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _nuevoInstrumentoState = MutableStateFlow(NuevoInstrumentoState())
    val nuevoInstrumentoState: StateFlow<NuevoInstrumentoState> = _nuevoInstrumentoState.asStateFlow()

    /** Actualiza el tipo de instrumento seleccionado. */
    fun onTipoChanged(tipo: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(tipo = tipo)
    }

    /** Actualiza la marca del nuevo instrumento. */
    fun onMarcaChanged(marca: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(marca = marca, errorValidacion = null)
    }

    /** Actualiza el modelo del nuevo instrumento. */
    fun onModeloChanged(modelo: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(modelo = modelo, errorValidacion = null)
    }

    /** Actualiza el número de serie del nuevo instrumento. */
    fun onNumeroSerieChanged(serie: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(numeroSerie = serie, errorValidacion = null)
    }

    /** Actualiza la fecha de vencimiento de calibración del nuevo instrumento. */
    fun onVencimientoCalibracionChanged(venc: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(vencimientoCalibracion = venc)
    }

    /**
     * Valida y crea un nuevo instrumento en el sistema, guardándolo mediante [InstrumentoRepository].
     * @param onSuccess Callback ejecutado al crear exitosamente el instrumento.
     */
    fun crearInstrumento(onSuccess: () -> Unit) {
        val state = _nuevoInstrumentoState.value
        if (state.marca.isBlank() || state.modelo.isBlank() || state.numeroSerie.isBlank()) {
            _nuevoInstrumentoState.value = state.copy(errorValidacion = "Marca, modelo y número de serie son obligatorios.")
            return
        }

        viewModelScope.launch {
            val nuevo = Instrumento(
                id = "INST-" + UUID.randomUUID().toString().take(6).uppercase(),
                tipo = state.tipo.trim(),
                marca = state.marca.trim(),
                modelo = state.modelo.trim(),
                numeroSerie = state.numeroSerie.trim(),
                vencimientoCalibracion = state.vencimientoCalibracion.trim(),
                activo = true
            )
            instrumentoRepository.insertInstrumento(nuevo)
            _nuevoInstrumentoState.value = NuevoInstrumentoState()
            onSuccess()
        }
    }

    /**
     * Renueva la fecha de calibración de un instrumento existente (RT-11).
     * @param instrumentoId ID único del instrumento.
     * @param nuevoVencimiento Nueva fecha de vencimiento.
     */
    fun renovarCalibracion(instrumentoId: String, nuevoVencimiento: String) {
        viewModelScope.launch {
            instrumentoRepository.actualizarCalibracion(instrumentoId, nuevoVencimiento)
        }
    }

    /**
     * RF-22: Activa o desactiva a un usuario del personal.
     * @param usuario Entidad [UsuarioEntity] a modificar.
     */
    fun toggleActivoPersonal(usuario: UsuarioEntity) {
        viewModelScope.launch {
            usuarioRepository.setActivo(usuario.id, !usuario.activo)
        }
    }
}
