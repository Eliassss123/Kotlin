// ============================================================================
// ARCHIVO : viewmodel/PruebaViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : maneja el formulario "Nueva prueba" (2 pantallas: formulario y resumen, comparten este mismo ViewModel)
//           y GUARDA la prueba + el protocolo en la base de datos.
// ============================================================================

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

// Todo el formulario en UN solo objeto inmutable (no se modifica: se crea una copia con .copy(...)).
// Los valores por defecto (= ...) son los que se ven al abrir el formulario.
// Nota: 'tecnico' está escrito fijo; no sale del usuario que inició sesión.
data class NuevaPruebaState(
    val equipoCodigo: String = "",
    val tipoPrueba: String = "Resistencia de Aislamiento (Megger)",
    val ubicacion: String = "",
    val fechaHora: String = "",
    val mediciones: List<Medicion> = listOf(
        Medicion("Resistencia Alta-Tierra", "4.2 GOhm"),
        Medicion("Indice de Polarizacion", "1.75")
    ),
    val estado: EstadoProtocolo = EstadoProtocolo.REGISTRADO_TERRENO,
    val observaciones: String = "",
    val tecnico: String = "Elias Farías (el.farr@duocuc.cl)"
)

// Usa 3 repositorios porque al guardar escribe en pruebas, protocolos y actualiza el equipo.
class PruebaViewModel(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository,
    private val protocoloRepository: ProtocoloRepository
) : ViewModel() {

    // Estado actual del formulario (privado y modificable) y su versión pública de solo lectura.
    private val _uiState = MutableStateFlow(NuevaPruebaState())
    val uiState: StateFlow<NuevaPruebaState> = _uiState.asStateFlow()

    // Equipos que se ofrecen en el menú desplegable.
    private val _equiposDisponibles = MutableStateFlow<List<Equipo>>(emptyList())
    val equiposDisponibles: StateFlow<List<Equipo>> = _equiposDisponibles.asStateFlow()

    // Al crearse: (1) pone la fecha y hora actuales (SimpleDateFormat) y (2) se suscribe a la lista de equipos;
    // si el formulario aún no tiene equipo elegido, selecciona el primero de la lista.
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

    // Elige un equipo. '.copy(...)' crea una copia del estado cambiando solo esos campos (así se 'modifica' un data
    //   class).
    fun setEquipo(equipo: Equipo) {
        _uiState.value = _uiState.value.copy(
            equipoCodigo = equipo.codigo,
            ubicacion = equipo.ubicacion
        )
    }

    // Si el usuario escribe el código a mano, se busca el equipo y, si existe, se autocompleta su ubicación.
    fun onEquipoCodigoChanged(codigo: String) {
        _uiState.value = _uiState.value.copy(equipoCodigo = codigo)
        viewModelScope.launch {
            val eq = equipoRepository.getEquipoByCodigo(codigo)
            if (eq != null) {
                _uiState.value = _uiState.value.copy(ubicacion = eq.ubicacion)
            }
        }
    }

    // Los siguientes 'on...Changed' son eventos de la UI: cada uno actualiza un campo del estado.
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

    // LÓGICA: solo agrega si campo y valor no están vacíos. Como el estado es inmutable, se copia la lista
    // (toMutableList), se agrega a la copia y se publica el nuevo estado.
    fun agregarMedicion(campo: String, valor: String) {
        if (campo.isNotBlank() && valor.isNotBlank()) {
            val listaActual = _uiState.value.mediciones.toMutableList()
            listaActual.add(Medicion(campo.trim(), valor.trim()))
            _uiState.value = _uiState.value.copy(mediciones = listaActual)
        }
    }

    // Quita la medición de esa posición si el índice es válido (index in indices).
    fun eliminarMedicion(index: Int) {
        val listaActual = _uiState.value.mediciones.toMutableList()
        if (index in listaActual.indices) {
            listaActual.removeAt(index)
            _uiState.value = _uiState.value.copy(mediciones = listaActual)
        }
    }

    // LÓGICA DE NEGOCIO PRINCIPAL. Pasos:
    //  1) genera ids y el código del protocolo con la hora actual (timestamp)
    //  2) arma una Prueba y un Protocolo con los datos del formulario
    //  3) los guarda en la base (Room)
    //  4) actualiza el 'último estado' del equipo
    //  5) avisa a la pantalla (onSuccess) para que navegue.
    // 'onSuccess: () -> Unit' es un callback: una función que la pantalla entrega y el ViewModel llama al terminar.
    fun guardarProtocolo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            // Hora actual en milisegundos: sirve para generar ids únicos.
            val timestamp = System.currentTimeMillis()
            val idPrueba = "PRU-$timestamp"
            val idProtocolo = "PROT-$timestamp"
            // Código con formato IND-PROT-<primeros 5 caracteres del equipo>-<timestamp>.
            val codigoProtocolo = "IND-PROT-${state.equipoCodigo.take(5)}-$timestamp"

            // Se crean 2 registros con los mismos datos: la Prueba (para el historial del equipo) y el Protocolo
            //   (informe guardado).
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

            // Escritura en la base de datos (Room).
            pruebaRepository.insertPrueba(nuevaPrueba)
            protocoloRepository.insertProtocolo(nuevoProtocolo)

            // El equipo queda con el estado de esta prueba (por eso cambia el color en la lista de equipos).
            // Actualizar ultimo estado del equipo si existe
            val equipo = equipoRepository.getEquipoByCodigo(state.equipoCodigo)
            if (equipo != null) {
                equipoRepository.insertEquipo(equipo.copy(ultimoEstado = state.estado))
            }

            // Avisa a la pantalla que terminó. Nota: el formulario NO se reinicia después de guardar (el estado sigue
            //   con los datos anteriores).
            onSuccess()
        }
    }
}
