









package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.ActaRepository
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.data.repository.VisitaRepository
import com.induztek.protocolos.domain.rules.ReglasNegocio
import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.SyncStatus
import com.induztek.protocolos.model.Visita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class NuevaVisitaState(
    val ordenTrabajo: String = "OT-2026-085",
    val cliente: String = "Planta Lampa",
    val instalacion: String = "Subestación Principal Norte",
    val modalidad: ModalidadVisita = ModalidadVisita.SOLO_PRUEBAS,
    val ingenieroACargo: String = "Carlos Mendoza (Ingeniero de Pruebas)",
    val tecnicos: String = "Juan Pérez (Técnico de Terreno)",
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val errorValidacion: String? = null
)


class VisitaViewModel(
    private val visitaRepository: VisitaRepository,
    private val protocoloRepository: ProtocoloRepository,
    private val pruebaRepository: PruebaRepository,
    private val equipoRepository: EquipoRepository,
    private val actaRepository: ActaRepository
) : ViewModel() {

    private val _nuevaVisitaState = MutableStateFlow(NuevaVisitaState())
    val nuevaVisitaState: StateFlow<NuevaVisitaState> = _nuevaVisitaState.asStateFlow()

    private val _visitaSeleccionada = MutableStateFlow<Visita?>(null)
    val visitaSeleccionada: StateFlow<Visita?> = _visitaSeleccionada.asStateFlow()

    private val _protocolosVisita = MutableStateFlow<List<Protocolo>>(emptyList())
    val protocolosVisita: StateFlow<List<Protocolo>> = _protocolosVisita.asStateFlow()

    private val _actaGenerada = MutableStateFlow<Acta?>(null)
    val actaGenerada: StateFlow<Acta?> = _actaGenerada.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init {
        viewModelScope.launch {
            visitaRepository.seedInitialDataIfEmpty()
            actaRepository.seedInitialDataIfEmpty()
            protocoloRepository.seedInitialDataIfEmpty()
            pruebaRepository.seedInitialDataIfEmpty()
        }
    }

    
    val todasLasVisitas: StateFlow<List<Visita>> = visitaRepository.allVisitas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onOrdenTrabajoChanged(ot: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(ordenTrabajo = ot, errorValidacion = null)
    }

    fun onClienteChanged(cliente: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(cliente = cliente, errorValidacion = null)
    }

    fun onInstalacionChanged(inst: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(instalacion = inst, errorValidacion = null)
    }

    fun onModalidadChanged(mod: ModalidadVisita) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(modalidad = mod)
    }

    fun onIngenieroACargoChanged(ing: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(ingenieroACargo = ing, errorValidacion = null)
    }

    fun onTecnicosChanged(tec: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(tecnicos = tec)
    }

    fun onTemperaturaAmbienteChanged(temp: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(temperaturaAmbiente = temp)
    }

    fun onHumedadChanged(hum: String) {
        _nuevaVisitaState.value = _nuevaVisitaState.value.copy(humedad = hum)
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    
    fun crearVisita(onSuccess: (String) -> Unit) {
        val state = _nuevaVisitaState.value
        if (state.ordenTrabajo.isBlank()) {
            _nuevaVisitaState.value = state.copy(errorValidacion = "La Orden de Trabajo (OT) es obligatoria")
            return
        }
        if (state.cliente.isBlank()) {
            _nuevaVisitaState.value = state.copy(errorValidacion = "El cliente es obligatorio")
            return
        }
        if (state.ingenieroACargo.isBlank()) {
            _nuevaVisitaState.value = state.copy(errorValidacion = "El Ingeniero a cargo es OBLIGATORIO (Aclar. 4 y 7)")
            return
        }

        viewModelScope.launch {
            val sdfFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfHora = SimpleDateFormat("HH:mm", Locale.getDefault())
            val hoy = Date()

            val nuevaVisita = Visita(
                id = "VIS-" + UUID.randomUUID().toString().take(8).uppercase(),
                ordenTrabajo = state.ordenTrabajo.trim(),
                fecha = sdfFecha.format(hoy),
                horaInicio = sdfHora.format(hoy),
                cliente = state.cliente.trim(),
                instalacion = state.instalacion.trim(),
                modalidad = state.modalidad,
                ingenieroACargo = state.ingenieroACargo.trim(),
                tecnicos = state.tecnicos.trim(),
                temperaturaAmbiente = state.temperaturaAmbiente.trim(),
                humedad = state.humedad.trim(),
                cerrada = false,
                syncStatus = SyncStatus.PENDIENTE
            )

            visitaRepository.insertVisita(nuevaVisita)
            cargarDetalleVisita(nuevaVisita.id)
            onSuccess(nuevaVisita.id)
        }
    }

    
    fun cargarDetalleVisita(visitaId: String) {
        viewModelScope.launch {
            val v = visitaRepository.getVisitaById(visitaId)
            _visitaSeleccionada.value = v
            if (v != null) {
                protocoloRepository.getProtocolosByVisita(v.id).collect { list ->
                    _protocolosVisita.value = list
                }
            }
        }
    }

    
    fun cargarActa(actaId: String) {
        viewModelScope.launch {
            val acta = actaRepository.getActaById(actaId)
            _actaGenerada.value = acta
            if (acta != null) {
                cargarDetalleVisita(acta.visitaId)
            }
        }
    }

    
    fun agregarEquipoAVisita(equipo: Equipo, tecnicoUsuario: String, onProtocoloCreado: (String) -> Unit) {
        val visita = _visitaSeleccionada.value ?: return

        viewModelScope.launch {
            val yaExiste = protocoloRepository.existeProtocoloParaEquipoEnVisita(visita.id, equipo.codigo)
            if (yaExiste) {
                _mensajeError.value = "Ya existe un protocolo registrado para el equipo ${equipo.codigo} en esta visita (RN-02)."
                return@launch
            }

            val timestamp = System.currentTimeMillis()
            val correlativo = (100..999).random()
            val numeroProtocolo = ReglasNegocio.generarNumeroProtocolo(correlativo)
            val idProtocolo = "PROT-$timestamp"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

            val nombresPruebas = ReglasNegocio.obtenerPruebasRequeridasPorTipo(equipo.tipo)
            val pruebasGeneradas = nombresPruebas.mapIndexed { idx, nombrePrueba ->
                Prueba(
                    id = "PRU-$timestamp-$idx",
                    protocoloId = idProtocolo,
                    ordenTrabajo = visita.ordenTrabajo,
                    equipoCodigo = equipo.codigo,
                    tipoPrueba = nombrePrueba,
                    ubicacion = "${visita.cliente} - ${visita.instalacion}",
                    fechaHora = sdf.format(Date()),
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = if (nombrePrueba.contains("TTR")) "Vanguard ATRT-03 S2" else if (nombrePrueba.contains("contacto")) "AEMC 6250" else if (nombrePrueba.contains("operación")) "Omicron CMC 356" else "Megger MIT525",
                    calibracionVigente = true,
                    temperaturaAmbiente = visita.temperaturaAmbiente,
                    humedad = visita.humedad,
                    mediciones = ReglasNegocio.generarMedicionesPlantillaPorPrueba(nombrePrueba),
                    observaciones = "Prueba generada automáticamente en terreno según norma técnica Induztek."
                )
            }

            pruebasGeneradas.forEach { pruebaRepository.insertPrueba(it) }

            val resultadoGlobal = ReglasNegocio.calcularResultadoGlobal(pruebasGeneradas)

            val nuevoProtocolo = Protocolo(
                id = idProtocolo,
                codigoProtocolo = numeroProtocolo,
                visitaId = visita.id,
                ordenTrabajo = visita.ordenTrabajo,
                equipoCodigo = equipo.codigo,
                tipoEquipo = equipo.tipo,
                fechaHora = sdf.format(Date()),
                estado = EstadoProtocolo.REGISTRADO_TERRENO,
                resultadoGlobal = resultadoGlobal,
                tecnico = tecnicoUsuario,
                tecnicoCreador = tecnicoUsuario,
                descripcionMantenimiento = if (visita.modalidad == ModalidadVisita.PRUEBAS_Y_MANTENIMIENTO) "Mantenimiento preventivo e inspección general de componentes." else "",
                instrumento = "Megger MIT525",
                calibracionVigente = true,
                temperaturaAmbiente = visita.temperaturaAmbiente,
                humedad = visita.humedad,
                actaEntregadaCliente = true,
                syncStatus = SyncStatus.PENDIENTE,
                observaciones = "Protocolo generado en terreno para ${equipo.codigo}.",
                pruebas = pruebasGeneradas,
                mediciones = pruebasGeneradas.flatMap { it.mediciones }
            )

            protocoloRepository.insertProtocolo(nuevoProtocolo)
            equipoRepository.insertEquipo(equipo.copy(ultimoEstado = EstadoProtocolo.REGISTRADO_TERRENO))

            onProtocoloCreado(idProtocolo)
        }
    }

    
    fun cerrarVisitaYGenerarActa(
        recibidoPor: String,
        onSuccess: (Acta) -> Unit,
        onError: (String) -> Unit
    ) {
        val visita = _visitaSeleccionada.value ?: run {
            onError("No hay una visita activa seleccionada")
            return
        }

        if (recibidoPor.isBlank()) {
            onError("Es obligatorio ingresar el nombre de la persona que recibe el acta (RT-08)")
            return
        }

        viewModelScope.launch {
            val protocolos = _protocolosVisita.value
            if (protocolos.isEmpty()) {
                onError("No se puede cerrar la visita sin al menos un protocolo registrado.")
                return@launch
            }

            for (prot in protocolos) {
                for (prueba in prot.pruebas) {
                    val errorPrueba = ReglasNegocio.validarPruebaCompleta(prueba)
                    if (errorPrueba != null) {
                        onError("Error en protocolo ${prot.codigoProtocolo}: $errorPrueba")
                        return@launch
                    }
                }
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val fechaEmision = sdf.format(Date())

            val resumenBuilder = StringBuilder()
            protocolos.forEach { p ->
                resumenBuilder.append("• Protocolo ${p.codigoProtocolo} (${p.equipoCodigo}): ${p.resultadoGlobal.label}\n")
                val noRealizadas = p.pruebas.filter { it.estadoPrueba == EstadoPrueba.NO_REALIZADA }
                if (noRealizadas.isNotEmpty()) {
                    noRealizadas.forEach { nr ->
                        resumenBuilder.append("   - NO REALIZADA: ${nr.tipoPrueba} (Motivo: ${nr.motivoNoRealizada})\n")
                    }
                }
            }

            val actaId = "ACTA-${visita.ordenTrabajo.replace("OT-", "")}"
            val nuevaActa = Acta(
                id = actaId,
                visitaId = visita.id,
                ordenTrabajo = visita.ordenTrabajo,
                cliente = visita.cliente,
                instalacion = visita.instalacion,
                fechaEmision = fechaEmision,
                tecnicoEntrega = visita.tecnicos,
                recibidoPor = recibidoPor.trim(),
                marcaPendienteFormalizacion = true,
                resumenProtocolos = resumenBuilder.toString().trim(),
                syncStatus = SyncStatus.PENDIENTE
            )

            actaRepository.insertActa(nuevaActa)
            visitaRepository.cerrarVisita(visita.id)
            _actaGenerada.value = nuevaActa

            onSuccess(nuevaActa)
        }
    }
}
