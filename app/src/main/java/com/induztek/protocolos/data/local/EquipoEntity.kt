// ============================================================================
// ARCHIVO : data/local/EquipoEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : la TABLA 'equipos'. Entity = formato en que se guarda en la base; Equipo (model) = formato que usa la
//           app.
//           toDomain() / fromDomain() convierten de uno a otro.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo

// @Entity: cada objeto de esta clase es una fila de la tabla 'equipos'.
@Entity(tableName = "equipos")
data class EquipoEntity(
    // Clave primaria: identifica cada fila de forma única.
    @PrimaryKey val id: String,
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    // En la base se guarda como TEXTO (el label del enum), no como enum.
    val ultimoEstado: String
) {
    // De fila de la BD -> objeto Equipo. Convierte el texto del estado en enum con fromLabel.
    fun toDomain(): Equipo {
        return Equipo(
            id = id,
            codigo = codigo,
            tipo = tipo,
            ubicacion = ubicacion,
            ultimoEstado = EstadoProtocolo.fromLabel(ultimoEstado)
        )
    }

    // companion object: funciones de la clase (no de cada objeto).
    companion object {
        // De objeto Equipo -> fila de la BD. El enum se guarda como su label (texto).
        fun fromDomain(equipo: Equipo): EquipoEntity {
            return EquipoEntity(
                id = equipo.id,
                codigo = equipo.codigo,
                tipo = equipo.tipo,
                ubicacion = equipo.ubicacion,
                ultimoEstado = equipo.ultimoEstado.label
            )
        }
    }
}
