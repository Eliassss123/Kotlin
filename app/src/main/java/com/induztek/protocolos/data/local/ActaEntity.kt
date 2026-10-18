// ============================================================================
// ARCHIVO : data/local/ActaEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'actas' según RT-08:
//           - Una sola acta por visita con todos sus protocolos
//           - Pruebas no realizadas con motivo
//           - Marca "Pendiente de formalización" y registro de quien recibe.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Acta
import com.induztek.protocolos.model.SyncStatus

/**
 * Entidad de Room que representa la tabla 'actas' en la base de datos local.
 * Almacena la información de entrega de protocolos por visita, técnico, cliente y estado de sincronización.
 */
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
    /**
     * Convierte la entidad de base de datos [ActaEntity] al modelo de dominio [Acta].
     * @return Objeto [Acta] correspondiente.
     */
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
        /**
         * Crea una instancia de [ActaEntity] a partir del modelo de dominio [Acta].
         * @param acta Modelo de dominio [Acta].
         * @return Entidad Room [ActaEntity].
         */
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
