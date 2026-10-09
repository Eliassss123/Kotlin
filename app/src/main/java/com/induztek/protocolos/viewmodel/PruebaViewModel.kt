// ============================================================================
// ARCHIVO : viewmodel/PruebaViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : maneja el formulario "Nueva prueba" y el resumen según Anexos 1, 2 y 3:
//           - Trazabilidad de instrumentos y calibración (PB-06)
//           - Condiciones ambientales y Orden de Trabajo (Paso 4)
//           - Técnico dinámico según usuario logueado (P-01 / P-02)
//           - Plantillas predefinidas de mediciones para agilizar trabajo con guantes (P-01 / PB-07)
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

data class NuevaPruebaState(
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String = "",
    val tipoPrueba: String = "Resistencia de Aislamiento (Megger)",
    val ubicacion: String = "",
    val fechaHora: String = "",
    val instrumento: String = "Megger MIT525 (Cal. Vigente 2026)",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val mediciones: List<Medicion> = listOf(
        Medicion("Resistencia Alta-Tierra (1min)", "4.5 GOhm"),
        Medicion("Resistencia Baja-Tierra (1min)", "3.8 GOhm"),
        Medicion("Índice de Polarización (IP)", "1.85"),
        Medicion("Tensión Aplicada", "2500 V")
    ),
    val estado: EstadoProtocolo = EstadoProtocolo.REGISTRADO_TERRENO,
    val observaciones: String = "",
    val tecnico: String = "Juan Pérez (Técnico de Terreno)"
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

    fun setUsuarioSesion(nombre: String, correo: String, rol: String) {
        val labelTecnico = "$nombre ($rol - $correo)"
        _uiState.value = _uiState.value.copy(tecnico = labelTecnico)
    }

    fun setEquipo(equipo: Equipo) {
        _uiState.value = _uiState.value.copy(
            equipoCodigo = equipo.codigo,
            ubicacion = "${equipo.cliente} - ${equipo.ubicacion}"
        )
    }

    fun onEquipoCodigoChanged(codigo: String) {
        _uiState.value = _uiState.value.copy(equipoCodigo = codigo)
        viewModelScope.launch {
            val eq = equipoRepository.getEquipoByCodigo(codigo)
            if (eq != null) {
                _uiState.value = _uiState.value.copy(ubicacion = "${eq.cliente} - ${eq.ubicacion}")
            }
        }
    }

    fun onOrdenTrabajoChanged(ot: String) {
        _uiState.value = _uiState.value.copy(ordenTrabajo = ot)
    }

    fun onTipoPruebaChanged(tipo: String) {
        val instrumentoRecomendado = when {
            tipo.contains("Megger", ignoreCase = true) -> "Megger MIT525 (Cal. Vigente 2026)"
            tipo.contains("TTR", ignoreCase = true) || tipo.contains("Transformación", ignoreCase = true) -> "Vanguard ATRT-03 (Cal. Vigente 2026)"
            tipo.contains("Microhm", ignoreCase = true) || tipo.contains("Contacto", ignoreCase = true) -> "AEMC 6250 Microhmómetro (Cal. Vigente 2026)"
            tipo.contains("51P", ignoreCase = true) || tipo.contains("Inyección", ignoreCase = true) -> "Omicron CMC 356 (Cal. Vigente 2026)"
            tipo.contains("VLF", ignoreCase = true) || tipo.contains("Hipot", ignoreCase = true) -> "HVA28 VLF Test Set (Cal. Vigente 2026)"
            tipo.contains("Termografía", ignoreCase = true) -> "Fluke Ti401 Pro Cámara IR (Cal. Vigente 2026)"
            else -> _uiState.value.instrumento
        }

        val medicionesPlantilla = obtenerPlantillaPorTipo(tipo)

        _uiState.value = _uiState.value.copy(
            tipoPrueba = tipo,
            instrumento = instrumentoRecomendado,
            mediciones = medicionesPlantilla
        )
    }

    fun cargarPlantillaRecomendada() {
        val plantilla = obtenerPlantillaPorTipo(_uiState.value.tipoPrueba)
        _uiState.value = _uiState.value.copy(mediciones = plantilla)
    }

    private fun obtenerPlantillaPorTipo(tipo: String): List<Medicion> {
        return when {
            tipo.contains("Megger", ignoreCase = true) -> listOf(
                Medicion("Resistencia Alta-Tierra (1min)", "4.5 GOhm"),
                Medicion("Resistencia Baja-Tierra (1min)", "3.8 GOhm"),
                Medicion("Índice de Polarización (IP)", "1.85"),
                Medicion("Tensión Aplicada", "2500 V")
            )
            tipo.contains("TTR", ignoreCase = true) || tipo.contains("Transformación", ignoreCase = true) -> listOf(
                Medicion("Relación Fase H1-H2 / X1-X2", "21.74"),
                Medicion("Error Relación %", "0.12%"),
                Medicion("Desplazamiento Angular", "0.0°")
            )
            tipo.contains("Microhm", ignoreCase = true) || tipo.contains("Contacto", ignoreCase = true) -> listOf(
                Medicion("Resistencia Contacto Fase R", "45 uOhm"),
                Medicion("Resistencia Contacto Fase S", "48 uOhm"),
                Medicion("Resistencia Contacto Fase T", "42 uOhm")
            )
            tipo.contains("51P", ignoreCase = true) || tipo.contains("Inyección", ignoreCase = true) -> listOf(
                Medicion("Corriente Recogida (I_pickup)", "5.02 A"),
                Medicion("Tiempo Disparo (1.5x I_pickup)", "0.42 s"),
                Medicion("Tensión Auxiliar DC", "125 V")
            )
            tipo.contains("VLF", ignoreCase = true) || tipo.contains("Hipot", ignoreCase = true) -> listOf(
                Medicion("Tensión VLF Aplicada (0.1 Hz)", "18 kV"),
                Medicion("Fuga Fase R (15 min)", "1.2 mA"),
                Medicion("Fuga Fase S (15 min)", "1.4 mA"),
                Medicion("Fuga Fase T (15 min)", "1.1 mA")
            )
            tipo.contains("Termografía", ignoreCase = true) -> listOf(
                Medicion("Temp Máx Fase R", "38.2°C"),
                Medicion("Temp Máx Fase S", "39.1°C"),
                Medicion("Temp Máx Fase T", "38.5°C"),
                Medicion("Delta T Sobre Ambiente", "16.5°C")
            )
            else -> _uiState.value.mediciones
        }
    }

    fun onInstrumentoChanged(inst: String) {
        _uiState.value = _uiState.value.copy(instrumento = inst)
    }

    fun onCalibracionVigenteChanged(vigente: Boolean) {
        _uiState.value = _uiState.value.copy(calibracionVigente = vigente)
    }

    fun onTemperaturaAmbienteChanged(temp: String) {
        _uiState.value = _uiState.value.copy(temperaturaAmbiente = temp)
    }

    fun onHumedadChanged(hum: String) {
        _uiState.value = _uiState.value.copy(humedad = hum)
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

    fun onTecnicoChanged(tecnico: String) {
        _uiState.value = _uiState.value.copy(tecnico = tecnico)
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
                ordenTrabajo = state.ordenTrabajo,
                equipoCodigo = state.equipoCodigo,
                tipoPrueba = state.tipoPrueba,
                ubicacion = state.ubicacion,
                fechaHora = state.fechaHora,
                instrumento = state.instrumento,
                calibracionVigente = state.calibracionVigente,
                temperaturaAmbiente = state.temperaturaAmbiente,
                humedad = state.humedad,
                mediciones = state.mediciones,
                estadoPrueba = com.induztek.protocolos.model.EstadoPrueba.REALIZADA,
                resultado = com.induztek.protocolos.model.ResultadoPrueba.fromEstado(state.estado),
                observaciones = state.observaciones
            )

            val nuevoProtocolo = Protocolo(
                id = idProtocolo,
                codigoProtocolo = codigoProtocolo,
                ordenTrabajo = state.ordenTrabajo,
                equipoCodigo = state.equipoCodigo,
                tipoEquipo = state.tipoPrueba,
                fechaHora = state.fechaHora,
                estado = state.estado,
                tecnico = state.tecnico,
                instrumento = state.instrumento,
                calibracionVigente = state.calibracionVigente,
                temperaturaAmbiente = state.temperaturaAmbiente,
                humedad = state.humedad,
                actaEntregadaCliente = true,
                observaciones = state.observaciones,
                mediciones = state.mediciones
            )

            pruebaRepository.insertPrueba(nuevaPrueba)
            protocoloRepository.insertProtocolo(nuevoProtocolo)

            val equipo = equipoRepository.getEquipoByCodigo(state.equipoCodigo)
            if (equipo != null) {
                equipoRepository.insertEquipo(equipo.copy(ultimoEstado = state.estado))
            }

            onSuccess()
        }
    }
}
