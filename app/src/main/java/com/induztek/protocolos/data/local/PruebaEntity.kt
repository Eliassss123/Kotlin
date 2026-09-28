// ============================================================================
// ARCHIVO : data/local/PruebaEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : la TABLA 'pruebas'. Igual que ProtocoloEntity: las mediciones se guardan como JSON.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Prueba

// @Entity: tabla 'pruebas'.
@Entity(tableName = "pruebas")
data class PruebaEntity(
    @PrimaryKey val id: String,
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    // Lista de mediciones convertida a texto JSON.
    val medicionesJson: String,
    val estado: String,
    val observaciones: String
) {
    // De fila de la BD -> Prueba (lee el JSON y lo convierte en lista).
    fun toDomain(): Prueba {
        val listType = object : TypeToken<List<Medicion>>() {}.type
        val medicionesList: List<Medicion> = Gson().fromJson(medicionesJson, listType) ?: emptyList()
        return Prueba(
            id = id,
            equipoCodigo = equipoCodigo,
            tipoPrueba = tipoPrueba,
            ubicacion = ubicacion,
            fechaHora = fechaHora,
            mediciones = medicionesList,
            estado = EstadoProtocolo.fromLabel(estado),
            observaciones = observaciones
        )
    }

    companion object {
        // De Prueba -> fila de la BD (convierte la lista en JSON).
        fun fromDomain(prueba: Prueba): PruebaEntity {
            return PruebaEntity(
                id = prueba.id,
                equipoCodigo = prueba.equipoCodigo,
                tipoPrueba = prueba.tipoPrueba,
                ubicacion = prueba.ubicacion,
                fechaHora = prueba.fechaHora,
                medicionesJson = Gson().toJson(prueba.mediciones),
                estado = prueba.estado.label,
                observaciones = prueba.observaciones
            )
        }
    }
}
