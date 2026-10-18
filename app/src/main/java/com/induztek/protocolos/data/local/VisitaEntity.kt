// ============================================================================
// ARCHIVO : data/local/VisitaEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'visitas' según RT-01, Aclaraciones 4 y 7:
//           - OT, fecha, hora inicio, cliente, instalación, modalidad, ingeniero a cargo obligatorio,
//             técnicos, condiciones ambientales, estado de cierre y sync_status.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.SyncStatus
import com.induztek.protocolos.model.Visita

/**
 * Entidad de Room que representa la tabla 'visitas' en la base de datos local.
 * Almacena los datos de la visita en terreno, orden de trabajo, ingeniero a cargo y estado de cierre.
 */
@Entity(tableName = "visitas")
data class VisitaEntity(
    @PrimaryKey val id: String,
    val ordenTrabajo: String,
    val fecha: String,
    val horaInicio: String,
    val cliente: String,
    val instalacion: String,
    val modalidad: String,
    val ingenieroACargo: String,
    val tecnicos: String,
    val temperaturaAmbiente: String,
    val humedad: String,
    val cerrada: Boolean,
    val syncStatus: String
) {
    /**
     * Convierte la entidad [VisitaEntity] al modelo de dominio [Visita].
     */
    fun toDomain(): Visita {
        return Visita(
            id = id,
            ordenTrabajo = ordenTrabajo,
            fecha = fecha,
            horaInicio = horaInicio,
            cliente = cliente,
            instalacion = instalacion,
            modalidad = ModalidadVisita.fromLabel(modalidad),
            ingenieroACargo = ingenieroACargo,
            tecnicos = tecnicos,
            temperaturaAmbiente = temperaturaAmbiente,
            humedad = humedad,
            cerrada = cerrada,
            syncStatus = SyncStatus.fromLabel(syncStatus)
        )
    }

    companion object {
        /**
         * Crea una instancia de [VisitaEntity] a partir del modelo de dominio [Visita].
         */
        fun fromDomain(visita: Visita): VisitaEntity {
            return VisitaEntity(
                id = visita.id,
                ordenTrabajo = visita.ordenTrabajo,
                fecha = visita.fecha,
                horaInicio = visita.horaInicio,
                cliente = visita.cliente,
                instalacion = visita.instalacion,
                modalidad = visita.modalidad.label,
                ingenieroACargo = visita.ingenieroACargo,
                tecnicos = visita.tecnicos,
                temperaturaAmbiente = visita.temperaturaAmbiente,
                humedad = visita.humedad,
                cerrada = visita.cerrada,
                syncStatus = visita.syncStatus.label
            )
        }
    }
}
