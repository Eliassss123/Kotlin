// ============================================================================
// ARCHIVO : data/repository/ProtocoloRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Acceso a protocolos con soporte para formalización (RN-04, RN-16, RT-09),
//           unicidad (visita, equipo) (RN-02, Aclar. 5) y filtros para cliente (P-04).
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.ProtocoloDao
import com.induztek.protocolos.data.local.ProtocoloEntity
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProtocoloRepository(
    private val protocoloDao: ProtocoloDao
) {
    val allProtocolos: Flow<List<Protocolo>> = protocoloDao.getAllProtocolos().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getProtocolosByVisita(visitaId: String): Flow<List<Protocolo>> {
        return protocoloDao.getProtocolosByVisita(visitaId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getProtocolosByEquipo(equipoCodigo: String): Flow<List<Protocolo>> {
        return protocoloDao.getProtocolosByEquipo(equipoCodigo).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getProtocoloById(id: String): Protocolo? {
        return protocoloDao.getProtocoloById(id)?.toDomain()
    }

    // RN-02, Aclaración 5: Bloquear segundo protocolo del mismo equipo en la visita
    suspend fun existeProtocoloParaEquipoEnVisita(visitaId: String, equipoCodigo: String): Boolean {
        return protocoloDao.getProtocoloByVisitaYEquipo(visitaId, equipoCodigo) != null
    }

    suspend fun insertProtocolo(protocolo: Protocolo) {
        protocoloDao.insertProtocolo(ProtocoloEntity.fromDomain(protocolo))
    }

    // RN-04, RN-16, RT-09: Formalización manual por Ingeniero (P-02) o Jefe Técnico (P-03)
    suspend fun formalizarProtocolo(id: String, usuarioFormalizador: String, nuevoEstado: EstadoProtocolo) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val fecha = sdf.format(Date())
        protocoloDao.formalizarProtocolo(
            id = id,
            nuevoEstado = nuevoEstado.label,
            formalizadoPor = usuarioFormalizador,
            fechaFormalizacion = fecha
        )
    }

    suspend fun actualizarArchivoBlob(id: String, blob: ByteArray?) {
        protocoloDao.actualizarArchivoBlob(id, blob)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (protocoloDao.getCount() == 0) {
            val initialProtocolos = listOf(
                Protocolo(
                    id = "PROT-2026-001",
                    codigoProtocolo = "PT-2026-0001",
                    visitaId = "VIS-2026-001",
                    ordenTrabajo = "OT-2026-081",
                    equipoCodigo = "TR-500KVA-01",
                    tipoEquipo = "Transformador de Poder",
                    fechaHora = "2026-08-15 10:30",
                    estado = EstadoProtocolo.FORMALIZADO,
                    resultadoGlobal = ResultadoPrueba.APROBADO,
                    tecnico = "Juan Pérez (Técnico de Terreno)",
                    tecnicoCreador = "Juan Pérez",
                    descripcionMantenimiento = "Limpieza de bornes y reapriete de conexiones en alta y baja tensión.",
                    instrumento = "Megger MIT525",
                    calibracionVigente = true,
                    temperaturaAmbiente = "21°C",
                    humedad = "42%",
                    actaEntregadaCliente = true,
                    formalizadoPor = "Carlos Mendoza (Ingeniero de Pruebas)",
                    fechaFormalizacion = "2026-08-16 14:00",
                    syncStatus = SyncStatus.ENVIADO,
                    observaciones = "Protocolo de transformador formalizado y conforme.",
                    mediciones = listOf(
                        Medicion(campo = "Lectura 1 min (R1min)", valorNumerico = 4.5, unidad = "GΩ"),
                        Medicion(campo = "Índice de Polarización (IP)", valorNumerico = 1.84, unidad = "ratio"),
                        Medicion(campo = "Error Relación TTR Máx", valorNumerico = 0.09, unidad = "%")
                    )
                ),
                Protocolo(
                    id = "PROT-2026-002",
                    codigoProtocolo = "PT-2026-0002",
                    visitaId = "VIS-2026-001",
                    ordenTrabajo = "OT-2026-082",
                    equipoCodigo = "REL-SEL751-02",
                    tipoEquipo = "Relé de Protección",
                    fechaHora = "2026-09-01 11:00",
                    estado = EstadoProtocolo.REGISTRADO_TERRENO,
                    resultadoGlobal = ResultadoPrueba.APROBADO,
                    tecnico = "Juan Pérez (Técnico de Terreno)",
                    tecnicoCreador = "Juan Pérez",
                    descripcionMantenimiento = "Pruebas funcionales de disparo sobre interruptor principal.",
                    instrumento = "Omicron CMC 356",
                    calibracionVigente = true,
                    temperaturaAmbiente = "23°C",
                    humedad = "40%",
                    actaEntregadaCliente = true,
                    syncStatus = SyncStatus.PENDIENTE,
                    observaciones = "Inyección secundaria completada en terreno. Pendiente de formalización por ingeniero.",
                    mediciones = listOf(
                        Medicion(campo = "Corriente Inyectada", valorNumerico = 5.02, unidad = "A"),
                        Medicion(campo = "Tiempo Disparo", valorNumerico = 0.41, unidad = "s")
                    )
                ),
                Protocolo(
                    id = "PROT-2026-003",
                    codigoProtocolo = "PT-2026-0003",
                    visitaId = "VIS-2026-002",
                    ordenTrabajo = "OT-2026-083",
                    equipoCodigo = "CEL-MT24-03",
                    tipoEquipo = "Celda de Media Tensión",
                    fechaHora = "2026-08-20 16:20",
                    estado = EstadoProtocolo.OBSERVADO,
                    resultadoGlobal = ResultadoPrueba.OBSERVADO,
                    tecnico = "Juan Pérez (Técnico de Terreno)",
                    tecnicoCreador = "Juan Pérez",
                    descripcionMantenimiento = "Reapriete general y ajuste de tulipas de contacto.",
                    instrumento = "AEMC 6250",
                    calibracionVigente = true,
                    temperaturaAmbiente = "25°C",
                    humedad = "38%",
                    actaEntregadaCliente = true,
                    formalizadoPor = "Roberto Silva (Jefe Técnico)",
                    fechaFormalizacion = "2026-08-21 09:30",
                    syncStatus = SyncStatus.ENVIADO,
                    observaciones = "Resistencia de contacto en Fase S (85 µΩ) observada. Variación superior a 25% respecto a período anterior.",
                    mediciones = listOf(
                        Medicion(campo = "Resistencia Fase S", valorNumerico = 85.0, unidad = "µΩ")
                    )
                )
            )
            protocoloDao.insertAll(initialProtocolos.map { ProtocoloEntity.fromDomain(it) })
        }
    }
}
