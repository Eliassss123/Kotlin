







package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.UserProfile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID


data class NuevoInstrumentoState(
    val tipo: String = "Medidor de Resistencia de Aislamiento",
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val vencimientoCalibracion: String = "2027-12-31",
    val errorValidacion: String? = null
)


class AdminViewModel(
    private val instrumentoRepository: InstrumentoRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {


    val instrumentos: StateFlow<List<Instrumento>> = instrumentoRepository.allInstrumentos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val personal: StateFlow<List<UserProfile>> = usuarioRepository.allUsuarios
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _nuevoInstrumentoState = MutableStateFlow(NuevoInstrumentoState())
    val nuevoInstrumentoState: StateFlow<NuevoInstrumentoState> = _nuevoInstrumentoState.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            try {
                instrumentoRepository.refresh()
                usuarioRepository.refresh()
                _error.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.localizedMessage ?: "No fue posible cargar los datos de administración"
            }
        }
    }


    fun onTipoChanged(tipo: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(tipo = tipo)
    }


    fun onMarcaChanged(marca: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(marca = marca, errorValidacion = null)
    }


    fun onModeloChanged(modelo: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(modelo = modelo, errorValidacion = null)
    }


    fun onNumeroSerieChanged(serie: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(numeroSerie = serie, errorValidacion = null)
    }


    fun onVencimientoCalibracionChanged(venc: String) {
        _nuevoInstrumentoState.value = _nuevoInstrumentoState.value.copy(vencimientoCalibracion = venc)
    }


    fun crearInstrumento(onSuccess: () -> Unit) {
        val state = _nuevoInstrumentoState.value
        if (state.marca.isBlank() || state.modelo.isBlank() || state.numeroSerie.isBlank()) {
            _nuevoInstrumentoState.value = state.copy(errorValidacion = "Marca, modelo y número de serie son obligatorios.")
            return
        }

        viewModelScope.launch {
            try {
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
                _error.value = null
                onSuccess()
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.localizedMessage ?: "No fue posible guardar el instrumento"
            }
        }
    }


    fun renovarCalibracion(instrumentoId: String, nuevoVencimiento: String) {
        viewModelScope.launch {
            try {
                instrumentoRepository.actualizarCalibracion(instrumentoId, nuevoVencimiento)
                _error.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.localizedMessage ?: "No fue posible actualizar la calibración"
            }
        }
    }


    fun toggleActivoPersonal(usuario: UserProfile) {
        viewModelScope.launch {
            try {
                usuarioRepository.setActivo(usuario.id, !usuario.activo)
                _error.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.localizedMessage ?: "No fue posible actualizar el usuario"
            }
        }
    }
}
