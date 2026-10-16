// ============================================================================
// ARCHIVO : data/repository/VisitaRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Gestión del ciclo de visitas técnicas según RT-01, RT-08 y Aclaraciones 4 y 7.
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.VisitaDao
import com.induztek.protocolos.data.local.VisitaEntity
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.SyncStatus
import com.induztek.protocolos.model.Visita
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VisitaRepository(
    private val visitaDao: VisitaDao
) {
    val allVisitas: Flow<List<Visita>> = visitaDao.getAllVisitas().map { entities ->
        entities.map { it.toDomain() }
    }

    val visitaActiva: Flow<Visita?> = visitaDao.getVisitaActiva().map { it?.toDomain() }

    suspend fun getVisitaById(id: String): Visita? {
        return visitaDao.getVisitaById(id)?.toDomain()
    }

    suspend fun insertVisita(visita: Visita) {
        visitaDao.insertVisita(VisitaEntity.fromDomain(visita))
    }

    suspend fun cerrarVisita(id: String) {
        val entity = visitaDao.getVisitaById(id)
        if (entity != null) {
            visitaDao.updateVisita(entity.copy(cerrada = true))
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        if (visitaDao.getCount() == 0) {
            val initialVisitas = listOf(
                Visita(
                    id = "VIS-2026-001",
                    ordenTrabajo = "OT-2026-084",
                    fecha = "2026-10-08",
                    horaInicio = "08:30",
                    cliente = "Planta Lampa",
                    instalacion = "Subestación Principal Norte",
                    modalidad = ModalidadVisita.PRUEBAS_Y_MANTENIMIENTO,
                    ingenieroACargo = "Carlos Mendoza (P-02)",
                    tecnicos = "Juan Pérez (P-01), Lucas Valenzuela",
                    temperaturaAmbiente = "21°C",
                    humedad = "42%",
                    cerrada = false,
                    syncStatus = SyncStatus.PENDIENTE
                ),
                Visita(
                    id = "VIS-2026-002",
                    ordenTrabajo = "OT-2026-079",
                    fecha = "2026-09-20",
                    horaInicio = "09:00",
                    cliente = "Agroindustria Rancagua",
                    instalacion = "Subestación Distribución Sur",
                    modalidad = ModalidadVisita.SOLO_PRUEBAS,
                    ingenieroACargo = "Roberto Silva (P-03)",
                    tecnicos = "Juan Pérez (P-01)",
                    temperaturaAmbiente = "24°C",
                    humedad = "38%",
                    cerrada = true,
                    syncStatus = SyncStatus.ENVIADO
                )
            )
            visitaDao.insertAll(initialVisitas.map { VisitaEntity.fromDomain(it) })
        }
    }
}
