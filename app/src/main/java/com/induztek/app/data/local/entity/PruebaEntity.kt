package com.induztek.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla "pruebas".
 *
 * Relación con Equipo:
 * - ForeignKey garantiza integridad referencial a nivel SQLite.
 * - onDelete = CASCADE → si se elimina un equipo, sus pruebas se eliminan.
 * - Index en equipoId → mejora performance de queries por equipo.
 *
 * medicionesJson: las mediciones son un Map<String,String> variable según
 * tipo de prueba. Se persiste como JSON serializado (Gson). Room no puede
 * mapear Map directamente; usamos un TypeConverter en la Database.
 *
 * fotoUriPath: ruta absoluta del archivo de foto en almacenamiento interno.
 * Puede ser null si el técnico no tomó foto.
 *
 * estado: refleja el ciclo offline-first:
 *   REGISTRADO_TERRENO → PENDIENTE_SYNC → SINCRONIZADO → FORMALIZADO
 */
@Entity(
    tableName = "pruebas",
    foreignKeys = [
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["equipoId"])]
)
data class PruebaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val equipoId: Long,
    val fecha: String,           // "2026-09-23"
    val hora: String,            // "14:30"
    val tecnico: String,
    val medicionesJson: String,  // JSON: {"resistencia":"2.3 Ω","tension":"13.2 kV"}
    val observaciones: String = "",
    val fotoUriPath: String? = null,
    val estado: String = "REGISTRADO_TERRENO",   // nombre del enum EstadoPrueba
    val sincronizadoAt: String? = null
)
