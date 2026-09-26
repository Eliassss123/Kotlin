package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo

@Entity(tableName = "protocolos")
data class ProtocoloEntity(
    @PrimaryKey val id: String,
    val codigoProtocolo: String,
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: String,
    val tecnico: String,
    val observaciones: String,
    val medicionesJson: String
) {
    fun toDomain(): Protocolo {
        val listType = object : TypeToken<List<Medicion>>() {}.type
        val medicionesList: List<Medicion> = Gson().fromJson(medicionesJson, listType) ?: emptyList()
        return Protocolo(
            id = id,
            codigoProtocolo = codigoProtocolo,
            equipoCodigo = equipoCodigo,
            tipoEquipo = tipoEquipo,
            fechaHora = fechaHora,
            estado = EstadoProtocolo.fromLabel(estado),
            tecnico = tecnico,
            observaciones = observaciones,
            mediciones = medicionesList
        )
    }

    companion object {
        fun fromDomain(protocolo: Protocolo): ProtocoloEntity {
            return ProtocoloEntity(
                id = protocolo.id,
                codigoProtocolo = protocolo.codigoProtocolo,
                equipoCodigo = protocolo.equipoCodigo,
                tipoEquipo = protocolo.tipoEquipo,
                fechaHora = protocolo.fechaHora,
                estado = protocolo.estado.label,
                tecnico = protocolo.tecnico,
                observaciones = protocolo.observaciones,
                medicionesJson = Gson().toJson(protocolo.mediciones)
            )
        }
    }
}
