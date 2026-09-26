package com.induztek.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla "equipos".
 *
 * Decisión: el tipo se persiste como String (nombre del enum TipoEquipo).
 * Room convierte enum → String automáticamente con @TypeConverter, o bien
 * podemos almacenarlo directamente como String aquí y convertirlo en el Mapper.
 * Optamos por String en la entidad para simplicidad y legibilidad en SQLite.
 *
 * @PrimaryKey(autoGenerate = true) → Room genera el ID automáticamente.
 */
@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigo: String,          // ej. "TRF-001"
    val tipo: String,            // nombre del enum: "TRANSFORMADOR", "RELE", etc.
    val instalacion: String,     // nombre del cliente / sitio
    val descripcion: String = ""
)
