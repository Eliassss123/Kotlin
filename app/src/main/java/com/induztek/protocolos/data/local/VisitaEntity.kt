







package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.SyncStatus
import com.induztek.protocolos.model.Visita


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
