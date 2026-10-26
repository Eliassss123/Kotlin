








package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.SyncStatus


@Entity(tableName = "actas")
data class ActaEntity(
    @PrimaryKey val id: String,
    val visitaId: String,
    val ordenTrabajo: String,
    val cliente: String,
    val instalacion: String,
    val fechaEmision: String,
    val tecnicoEntrega: String,
    val recibidoPor: String,
    val marcaPendienteFormalizacion: Boolean,
    val resumenProtocolos: String,
    val syncStatus: String
) {
    
    fun toDomain(): Acta {
        return Acta(
            id = id,
            visitaId = visitaId,
            ordenTrabajo = ordenTrabajo,
            cliente = cliente,
            instalacion = instalacion,
            fechaEmision = fechaEmision,
            tecnicoEntrega = tecnicoEntrega,
            recibidoPor = recibidoPor,
            marcaPendienteFormalizacion = marcaPendienteFormalizacion,
            resumenProtocolos = resumenProtocolos,
            syncStatus = SyncStatus.fromLabel(syncStatus)
        )
    }

    companion object {
        
        fun fromDomain(acta: Acta): ActaEntity {
            return ActaEntity(
                id = acta.id,
                visitaId = acta.visitaId,
                ordenTrabajo = acta.ordenTrabajo,
                cliente = acta.cliente,
                instalacion = acta.instalacion,
                fechaEmision = acta.fechaEmision,
                tecnicoEntrega = acta.tecnicoEntrega,
                recibidoPor = acta.recibidoPor,
                marcaPendienteFormalizacion = acta.marcaPendienteFormalizacion,
                resumenProtocolos = acta.resumenProtocolos,
                syncStatus = acta.syncStatus.label
            )
        }
    }
}
