// ============================================================================
// ARCHIVO : data/local/ProtocoloEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : la TABLA 'protocolos'. Room no sabe guardar una lista de objetos, así que las mediciones se convierten
//           a texto JSON (con la librería Gson) al guardar, y de vuelta a lista al leer.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Protocolo

// @Entity: tabla 'protocolos'.
@Entity(tableName = "protocolos")
data class ProtocoloEntity(
    @PrimaryKey val id: String,
    val codigoProtocolo: String,
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    // Estado guardado como texto (label del enum).
    val estado: String,
    val tecnico: String,
    val observaciones: String,
    // Las mediciones viven aquí como TEXTO JSON, p. ej. [{"campo":"...","valor":"..."}].
    val medicionesJson: String
) {
    // De fila de la BD -> Protocolo.
    fun toDomain(): Protocolo {
        // TypeToken: le dice a Gson 'quiero una List<Medicion>' (la información de tipos se pierde en tiempo de
        //   ejecución, por eso este truco).
        val listType = object : TypeToken<List<Medicion>>() {}.type
        // Gson().fromJson(...) convierte el texto JSON en lista. '?: emptyList()' = si falla o es null, lista vacía.
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
        // De Protocolo -> fila de la BD.
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
                // Gson().toJson(...) convierte la lista de mediciones en texto JSON para guardarla.
                medicionesJson = Gson().toJson(protocolo.mediciones)
            )
        }
    }
}
