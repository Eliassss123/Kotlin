package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: String,
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    val ultimoEstado: String
) {
    fun toDomain(): Equipo {
        return Equipo(
            id = id,
            codigo = codigo,
            tipo = tipo,
            ubicacion = ubicacion,
            ultimoEstado = EstadoProtocolo.fromLabel(ultimoEstado)
        )
    }

    companion object {
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
