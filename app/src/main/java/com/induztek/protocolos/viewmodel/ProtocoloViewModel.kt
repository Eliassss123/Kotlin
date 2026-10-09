// ============================================================================
// ARCHIVO : viewmodel/ProtocoloViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : Gestión completa del protocolo, captura de mediciones (RT-04),
//           cálculos automáticos (RT-05), trazabilidad de instrumentos (RN-11),
//           comparación histórica (RN-09, RT-06) y formalización (RN-04, RN-16, RT-09).
// ============================================================================

package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.InstrumentoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.domain.rules.ReglasNegocio
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EdicionPruebaState(
    val pruebaId: String = "",
    val tipoPrueba: String = "",
    val estadoPrueba: EstadoPrueba = EstadoPrueba.REALIZADA,
    val motivoNoRealizada: String = "",
    val instrumentoSeleccionado: Instrumento? = null,
    val advertenciaCalibracionVencida: Boolean = false,
    val mediciones: List<Medicion> = emptyList(),
    val resultadoCalculado: ResultadoPrueba = ResultadoPrueba.APROBADO,
    val esAsignacionManual: Boolean = false,
    val resultadoManual: ResultadoPrueba = ResultadoPrueba.APROBADO,
    val variacionHistorica: Double? = null,
    val superaVariacion25: Boolean = false,
    val valorAnteriorHistorico: Double? = null,
    val observaciones: String = ""
)

class ProtocoloViewModel(
    private val protocoloRepository: ProtocoloRepository,
    private val pruebaRepository: PruebaRepository,
    private val instrumentoRepository: InstrumentoRepository,
    private val equipoRepository: EquipoRepository
) : ViewModel() {

    private val _empresaClienteFiltro = MutableStateFlow<String?>(null)
    val empresaClienteFiltro: StateFlow<String?> = _empresaClienteFiltro.asStateFlow()

    private val _selectedProtocolo = MutableStateFlow<Protocolo?>(null)
    val selectedProtocolo: StateFlow<Protocolo?> = _selectedProtocolo.asStateFlow()

    private val _pruebasDelProtocolo = MutableStateFlow<List<Prueba>>(emptyList())
    val pruebasDelProtocolo: StateFlow<List<Prueba>> = _pruebasDelProtocolo.asStateFlow()

    private val _instrumentosDisponibles = MutableStateFlow<List<Instrumento>>(emptyList())
    val instrumentosDisponibles: StateFlow<List<Instrumento>> = _instrumentosDisponibles.asStateFlow()

    private val _edicionPruebaState = MutableStateFlow<EdicionPruebaState?>(null)
    val edicionPruebaState: StateFlow<EdicionPruebaState?> = _edicionPruebaState.asStateFlow()

    init {
        viewModelScope.launch {
            protocoloRepository.seedInitialDataIfEmpty()
            pruebaRepository.seedInitialDataIfEmpty()
            instrumentoRepository.seedInitialDataIfEmpty()

            instrumentoRepository.instrumentosActivos.collect { list ->
                _instrumentosDisponibles.value = list
            }
        }
    }

    // Lista de protocolos con filtro por empresa cliente para P-04 (RT-10)
    val protocolos: StateFlow<List<Protocolo>> = combine(
        protocoloRepository.allProtocolos,
        _empresaClienteFiltro
    ) { lista, empresaCliente ->
        if (!empresaCliente.isNullOrBlank()) {
            lista.filter {
                val eq = equipoRepository.getEquipoByCodigo(it.equipoCodigo)
                eq?.cliente?.equals(empresaCliente, ignoreCase = true) ?: true
            }
        } else {
            lista
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setEmpresaCliente(empresa: String?) {
        _empresaClienteFiltro.value = empresa
    }

    fun cargarProtocolo(id: String) {
        viewModelScope.launch {
            val prot = protocoloRepository.getProtocoloById(id)
            _selectedProtocolo.value = prot
            if (prot != null) {
                pruebaRepository.getPruebasByProtocolo(prot.id).collect { list ->
                    _pruebasDelProtocolo.value = if (list.isNotEmpty()) list else prot.pruebas
                }
            }
        }
    }

    // Preparar edición de una prueba técnica por ID
    fun iniciarEdicionPrueba(pruebaId: String) {
        viewModelScope.launch {
            val p = pruebaRepository.getPruebaById(pruebaId)
            if (p != null) {
                iniciarEdicionPrueba(p)
            }
        }
    }

    // Preparar edición de una prueba técnica
    fun iniciarEdicionPrueba(prueba: Prueba) {
        viewModelScope.launch {
            val esManual = ReglasNegocio.esPruebaConAsignacionManual(prueba.tipoPrueba)
            val inst = _instrumentosDisponibles.value.find { it.modelo.contains(prueba.instrumento) || prueba.instrumento.contains(it.modelo) }
                ?: _instrumentosDisponibles.value.firstOrNull()

            // RT-06, RN-09: Buscar medición anterior del mismo tipo de prueba para comparación histórica
            val ultimaPrevia = pruebaRepository.getUltimaPruebaPorTipo(prueba.equipoCodigo, prueba.tipoPrueba)
            val valorAnterior = ultimaPrevia?.mediciones?.firstOrNull { it.valorNumerico != null }?.valorNumerico

            val valorActual = prueba.mediciones.firstOrNull { it.valorNumerico != null }?.valorNumerico

            var variacion: Double? = null
            var supera25 = false
            if (valorActual != null && valorAnterior != null && valorAnterior > 0.0) {
                val pair = ReglasNegocio.calcularVariacionHistorica(valorActual, valorAnterior)
                variacion = pair.first
                supera25 = pair.second
            }

            _edicionPruebaState.value = EdicionPruebaState(
                pruebaId = prueba.id,
                tipoPrueba = prueba.tipoPrueba,
                estadoPrueba = prueba.estadoPrueba,
                motivoNoRealizada = prueba.motivoNoRealizada,
                instrumentoSeleccionado = inst,
                advertenciaCalibracionVencida = inst != null && !inst.estaCalibracionVigente(),
                mediciones = if (prueba.mediciones.isNotEmpty()) prueba.mediciones else ReglasNegocio.generarMedicionesPlantillaPorPrueba(prueba.tipoPrueba),
                resultadoCalculado = prueba.resultado,
                esAsignacionManual = esManual,
                resultadoManual = prueba.resultado,
                variacionHistorica = variacion,
                superaVariacion25 = supera25,
                valorAnteriorHistorico = valorAnterior,
                observaciones = prueba.observaciones
            )
        }
    }

    fun onEstadoPruebaChanged(estado: EstadoPrueba) {
        _edicionPruebaState.value = _edicionPruebaState.value?.copy(estadoPrueba = estado)
        recalcularResultadoPrueba()
    }

    fun onMotivoNoRealizadaChanged(motivo: String) {
        _edicionPruebaState.value = _edicionPruebaState.value?.copy(motivoNoRealizada = motivo)
    }

    fun onInstrumentoSeleccionado(inst: Instrumento) {
        val vencida = !inst.estaCalibracionVigente()
        _edicionPruebaState.value = _edicionPruebaState.value?.copy(
            instrumentoSeleccionado = inst,
            advertenciaCalibracionVencida = vencida
        )
    }

    fun onObservacionesPruebaChanged(obs: String) {
        _edicionPruebaState.value = _edicionPruebaState.value?.copy(observaciones = obs)
    }

    fun onResultadoManualChanged(res: ResultadoPrueba) {
        _edicionPruebaState.value = _edicionPruebaState.value?.copy(resultadoManual = res)
    }

    // Actualizar valor de medición con recálculo automático inmediato (RT-05)
    fun actualizarMedicion(index: Int, nuevoValorNum: Double?, nuevaUnidad: String, momento: MomentoMedicion) {
        val current = _edicionPruebaState.value ?: return
        val lista = current.mediciones.toMutableList()
        if (index in lista.indices) {
            val med = lista[index]
            lista[index] = med.copy(valorNumerico = nuevoValorNum, unidad = nuevaUnidad, momento = momento)
            _edicionPruebaState.value = current.copy(mediciones = lista)
            recalcularResultadoPrueba()
        }
    }

    fun agregarMedicion(campo: String, valorNum: Double?, unidad: String, momento: MomentoMedicion) {
        val current = _edicionPruebaState.value ?: return
        val lista = current.mediciones.toMutableList()
        lista.add(Medicion(campo = campo.trim(), valorNumerico = valorNum, unidad = unidad.trim(), momento = momento))
        _edicionPruebaState.value = current.copy(mediciones = lista)
        recalcularResultadoPrueba()
    }

    fun eliminarMedicion(index: Int) {
        val current = _edicionPruebaState.value ?: return
        val lista = current.mediciones.toMutableList()
        if (index in lista.indices) {
            lista.removeAt(index)
            _edicionPruebaState.value = current.copy(mediciones = lista)
            recalcularResultadoPrueba()
        }
    }

    // RT-05: Cálculos automáticos por reglas de negocio
    private fun recalcularResultadoPrueba() {
        val state = _edicionPruebaState.value ?: return
        if (state.estadoPrueba == EstadoPrueba.NO_REALIZADA) {
            _edicionPruebaState.value = state.copy(resultadoCalculado = ResultadoPrueba.NO_REALIZADA)
            return
        }

        if (state.esAsignacionManual) {
            _edicionPruebaState.value = state.copy(resultadoCalculado = state.resultadoManual)
            return
        }

        val tipo = state.tipoPrueba.lowercase()
        val mediciones = state.mediciones

        var nuevoResultado = ResultadoPrueba.APROBADO

        when {
            // Aislación: IP = 10 min / 1 min
            tipo.contains("aislación") || tipo.contains("aislamiento") -> {
                val med1min = ReglasNegocio.filtrarMedicionVigente(mediciones, "Lectura 1 min (R1min)")?.valorNumerico ?: 4.5
                val med10min = ReglasNegocio.filtrarMedicionVigente(mediciones, "Lectura 10 min (R10min)")?.valorNumerico ?: 8.5
                val (ip, res) = ReglasNegocio.calcularIndicePolarizacion(med1min, med10min)
                nuevoResultado = res

                // Actualizar o añadir IP en la lista de mediciones
                val listaActualizada = mediciones.map {
                    if (it.campo.contains("IP") || it.campo.contains("Polarización")) {
                        it.copy(valorNumerico = ip)
                    } else it
                }
                _edicionPruebaState.value = state.copy(mediciones = listaActualizada, resultadoCalculado = nuevoResultado)
                return
            }
            // TTR: Error % = ((medida - teórica) / teórica) * 100
            tipo.contains("ttr") || tipo.contains("transformación") -> {
                val teorica = ReglasNegocio.filtrarMedicionVigente(mediciones, "Relación Teórica (Placa)")?.valorNumerico ?: 21.74
                val medidaU = ReglasNegocio.filtrarMedicionVigente(mediciones, "Relación Medida (Fase U)")?.valorNumerico ?: 21.75
                val (error, res) = ReglasNegocio.calcularErrorTTR(teorica, medidaU)
                nuevoResultado = res

                val listaActualizada = mediciones.map {
                    if (it.campo.contains("Error")) {
                        it.copy(valorNumerico = error)
                    } else it
                }
                _edicionPruebaState.value = state.copy(mediciones = listaActualizada, resultadoCalculado = nuevoResultado)
                return
            }
            // Contactos: <= 50 µΩ Aprobado, 50-100 Observado, >100 Rechazado
            tipo.contains("contacto") -> {
                val maxResistencia = mediciones.mapNotNull { it.valorNumerico }.maxOrNull() ?: 45.0
                nuevoResultado = ReglasNegocio.evaluarResistenciaContactos(maxResistencia)
            }
            // Relé: Error tiempo de disparo
            tipo.contains("operación") || tipo.contains("inyección") -> {
                val teorico = ReglasNegocio.filtrarMedicionVigente(mediciones, "Tiempo Teórico de Curva")?.valorNumerico ?: 0.40
                val medido = ReglasNegocio.filtrarMedicionVigente(mediciones, "Tiempo Medido de Disparo")?.valorNumerico ?: 0.41
                val (error, res) = ReglasNegocio.evaluarOperacionRele(teorico, medido)
                nuevoResultado = res
            }
        }

        _edicionPruebaState.value = state.copy(resultadoCalculado = nuevoResultado)
    }

    // Guardar cambios de la prueba y recalcular resultado global del protocolo
    fun guardarEdicionPrueba(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _edicionPruebaState.value ?: return
        val prot = _selectedProtocolo.value ?: return

        // Validar no vacía si es NO_REALIZADA (Aclaración 2)
        if (state.estadoPrueba == EstadoPrueba.NO_REALIZADA && state.motivoNoRealizada.isBlank()) {
            onError("Si la prueba es NO REALIZADA, debe registrar obligatoriamente el motivo (Aclaración 2).")
            return
        }

        viewModelScope.launch {
            val inst = state.instrumentoSeleccionado
            val pruebaActualizada = Prueba(
                id = state.pruebaId,
                protocoloId = prot.id,
                ordenTrabajo = prot.ordenTrabajo,
                equipoCodigo = prot.equipoCodigo,
                tipoPrueba = state.tipoPrueba,
                ubicacion = prot.equipoCodigo,
                fechaHora = prot.fechaHora,
                estadoPrueba = state.estadoPrueba,
                motivoNoRealizada = state.motivoNoRealizada.trim(),
                resultado = state.resultadoCalculado,
                instrumentoId = inst?.id ?: "",
                instrumento = inst?.modelo ?: prot.instrumento,
                instrumentoSerie = inst?.numeroSerie ?: "SN-2024-551",
                instrumentoCalibracion = inst?.vencimientoCalibracion ?: "2026-12-31",
                calibracionVigente = inst?.estaCalibracionVigente() ?: true,
                temperaturaAmbiente = prot.temperaturaAmbiente,
                humedad = prot.humedad,
                mediciones = state.mediciones,
                observaciones = state.observaciones,
                variacionHistoricaPorcentaje = state.variacionHistorica,
                superaVariacion25 = state.superaVariacion25
            )

            pruebaRepository.insertPrueba(pruebaActualizada)

            // Recalcular lista de pruebas del protocolo y resultado global (RN-10)
            val listaPruebas = _pruebasDelProtocolo.value.map {
                if (it.id == pruebaActualizada.id) pruebaActualizada else it
            }
            val nuevoResultadoGlobal = ReglasNegocio.calcularResultadoGlobal(listaPruebas)

            val protocoloActualizado = prot.copy(
                resultadoGlobal = nuevoResultadoGlobal,
                pruebas = listaPruebas,
                mediciones = listaPruebas.flatMap { it.mediciones }
            )
            protocoloRepository.insertProtocolo(protocoloActualizado)
            _selectedProtocolo.value = protocoloActualizado
            _pruebasDelProtocolo.value = listaPruebas

            _edicionPruebaState.value = null
            onSuccess()
        }
    }

    // RN-04, RN-16, RT-09: Formalización del protocolo
    fun formalizarProtocolo(usuarioFormalizador: String, esAprobacion: Boolean, onSuccess: () -> Unit) {
        val prot = _selectedProtocolo.value ?: return
        viewModelScope.launch {
            val nuevoEstado = if (esAprobacion) EstadoProtocolo.FORMALIZADO else EstadoProtocolo.OBSERVADO
            protocoloRepository.formalizarProtocolo(prot.id, usuarioFormalizador, nuevoEstado)
            cargarProtocolo(prot.id)
            onSuccess()
        }
    }
}
