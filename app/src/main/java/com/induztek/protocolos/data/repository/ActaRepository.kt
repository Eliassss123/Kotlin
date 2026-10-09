// ============================================================================
// ARCHIVO : data/repository/ActaRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Gestión de actas de entrega de visita (RT-08).
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.ActaDao
import com.induztek.protocolos.data.local.ActaEntity
import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActaRepository(
    private val actaDao: ActaDao
) {
    val allActas: Flow<List<Acta>> = actaDao.getAllActas().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getActaById(id: String): Acta? {
        return actaDao.getActaById(id)?.toDomain()
    }

    suspend fun getActaByVisita(visitaId: String): Acta? {
        return actaDao.getActaByVisita(visitaId)?.toDomain()
    }

    suspend fun insertActa(acta: Acta) {
        actaDao.insertActa(ActaEntity.fromDomain(acta))
    }

    suspend fun seedInitialDataIfEmpty() {
        if (actaDao.getCount() == 0) {
            val initialActas = listOf(
                Acta(
                    id = "ACTA-2026-079",
                    visitaId = "VIS-2026-002",
                    ordenTrabajo = "OT-2026-079",
                    cliente = "Agroindustria Rancagua",
                    instalacion = "Subestación Distribución Sur",
                    fechaEmision = "2026-09-20 16:45",
                    tecnicoEntrega = "Juan Pérez (Técnico de Terreno)",
                    recibidoPor = "Guillermo Fuentes (Jefe Mantención Agroindustria)",
                    marcaPendienteFormalizacion = true,
                    resumenProtocolos = "Protocolo PT-2026-0003 (Celda MT CEL-MT24-03): Resultado OBSERVADO. Resistencia de contactos en fase S sobre límite.",
                    syncStatus = SyncStatus.ENVIADO
                )
            )
            actaDao.insertAll(initialActas.map { ActaEntity.fromDomain(it) })
        }
    }
}
