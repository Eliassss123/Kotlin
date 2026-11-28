package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.remote.toUserMessage
import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.domain.errores.CodigoError
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
                _error.value = exception.toUserMessage()
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
            _nuevoInstrumentoState.value = state.copy(errorValidacion = CodigoError.ADM_001.formatear())
            return
        }
        if (!esFechaValida(state.vencimientoCalibracion)) {
            _nuevoInstrumentoState.value = state.copy(errorValidacion = CodigoError.ADM_002.formatear(state.vencimientoCalibracion))
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
                _error.value = exception.toUserMessage()
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
                _error.value = exception.toUserMessage()
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
                _error.value = exception.toUserMessage()
            }
        }
    }

    fun crearUsuario(
        nombre: String,
        correo: String,
        password: String,
        empresaPlanta: String,
        roleCode: String,
        onSuccess: () -> Unit
    ) {
        if (nombre.isBlank() || correo.isBlank() || password.length < 10) {
            _error.value = CodigoError.ADM_003.formatear()
            return
        }
        if (!correo.trim().matches(CORREO_REGEX)) {
            _error.value = CodigoError.ADM_004.formatear(correo.trim())
            return
        }
        viewModelScope.launch {
            try {
                usuarioRepository.registerWithRoleAndCompany(
                    name = nombre.trim(),
                    email = correo.trim(),
                    password = password,
                    company = empresaPlanta.trim().ifBlank { null },
                    roleCode = roleCode,
                    termsAccepted = true
                )
                usuarioRepository.refresh()
                _error.value = null
                onSuccess()
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.toUserMessage()
            }
        }
    }

    private companion object {
        val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

        /** true si la fecha tiene formato yyyy-MM-dd y existe en el calendario (rechaza 2027-02-30). */
        fun esFechaValida(fecha: String): Boolean {
            val texto = fecha.trim()
            if (!texto.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return false
            // SimpleDateFormat estricto en vez de java.time: minSdk 24 no incluye java.time sin desugaring.
            val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT).apply { isLenient = false }
            return try {
                formato.parse(texto) != null
            } catch (_: java.text.ParseException) {
                false
            }
        }
    }

    fun eliminarUsuario(usuarioId: String) {
        viewModelScope.launch {
            try {
                usuarioRepository.eliminarUsuario(usuarioId)
                _error.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.toUserMessage()
            }
        }
    }

    fun cambiarRolPersonal(usuario: UserProfile, nuevoRolCodigo: String) {
        viewModelScope.launch {
            try {
                usuarioRepository.actualizarRol(usuario.id, nuevoRolCodigo)
                _error.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _error.value = exception.toUserMessage()
            }
        }
    }
}
