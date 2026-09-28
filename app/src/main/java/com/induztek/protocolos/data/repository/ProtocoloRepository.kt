// ============================================================================
// ARCHIVO : data/repository/ProtocoloRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : acceso a los protocolos guardados. Ver EquipoRepository.kt para la explicación general.
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.ProtocoloDao
import com.induztek.protocolos.data.local.ProtocoloEntity
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProtocoloRepository(
    private val protocoloDao: ProtocoloDao
) {
    // Flow con todos los protocolos (ordenados por fecha desde el DAO). Se actualiza solo.
    val allProtocolos: Flow<List<Protocolo>> = protocoloDao.getAllProtocolos().map { entities ->
        entities.map { it.toDomain() }
    }

    // Busca un protocolo por id (null si no existe).
    suspend fun getProtocoloById(id: String): Protocolo? {
        return protocoloDao.getProtocoloById(id)?.toDomain()
    }

    // Guarda un protocolo nuevo.
    suspend fun insertProtocolo(protocolo: Protocolo) {
        protocoloDao.insertProtocolo(ProtocoloEntity.fromDomain(protocolo))
    }

    // Siembra 3 protocolos de ejemplo si la tabla está vacía.
    suspend fun seedInitialDataIfEmpty() {
        if (protocoloDao.getCount() == 0) {
            val initialProtocolos = listOf(
                Protocolo(
                    id = "PROT-2026-001",
                    codigoProtocolo = "IND-PROT-TR01",
                    equipoCodigo = "TR-500KVA-01",
                    tipoEquipo = "Transformador de Potencia",
                    fechaHora = "2026-08-15 10:30",
                    estado = EstadoProtocolo.FORMALIZADO,
                    tecnico = "Elias Farías (el.farr@duocuc.cl)",
                    observaciones = "Prueba de aislamiento inicial completada y formalizada en sistema.",
                    mediciones = listOf(
                        Medicion("Resistencia Alta-Tierra", "4.5 GOhm"),
                        Medicion("Indice de Polarizacion", "1.85")
                    )
                ),
                Protocolo(
                    id = "PROT-2026-002",
                    codigoProtocolo = "IND-PROT-REL02",
                    equipoCodigo = "REL-SEL751-02",
                    tipoEquipo = "Relé de Protección",
                    fechaHora = "2026-09-01 11:00",
                    estado = EstadoProtocolo.REGISTRADO_TERRENO,
                    tecnico = "Elias Farías (el.farr@duocuc.cl)",
                    observaciones = "Inyección secundaria de corriente efectuada en terreno.",
                    mediciones = listOf(
                        Medicion("I_pickup 51P", "5.02 A"),
                        Medicion("Tiempo disparo", "0.42 s")
                    )
                ),
                Protocolo(
                    id = "PROT-2026-003",
                    codigoProtocolo = "IND-PROT-CEL03",
                    equipoCodigo = "CEL-MT24-03",
                    tipoEquipo = "Celda de Media Tensión",
                    fechaHora = "2026-08-20 16:20",
                    estado = EstadoProtocolo.FUERA_DE_RANGO,
                    tecnico = "Elias Farías (el.farr@duocuc.cl)",
                    observaciones = "Resistencia de contacto excedida en Fase S.",
                    mediciones = listOf(
                        Medicion("Resistencia Fase S", "120 uOhm")
                    )
                )
            )
            // Convierte y guarda todos los protocolos de ejemplo.
            protocoloDao.insertAll(initialProtocolos.map { ProtocoloEntity.fromDomain(it) })
        }
    }
}
