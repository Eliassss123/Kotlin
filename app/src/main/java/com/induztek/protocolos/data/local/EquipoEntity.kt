// ============================================================================
// ARCHIVO : data/local/EquipoEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'equipos' ampliada con datos de placa de características (FT-03 sec. 2)
//           y campos para validación de duplicados en terreno (RT-02).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: String,
    val codigo: String,
    val tagCliente: String = "",
    val tipo: String,
    val cliente: String = "",
    val ubicacion: String,
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val potenciaTension: String = "",
    val anio: String = "2020",
    val tensiones: String = "23 kV / 380 V",
    val tap: String = "Tap 3 (Normal)",
    val conexion: String = "Dyn11",
    val ultimoEstado: String
) {
    fun toDomain(): Equipo {
        return Equipo(
            id = id,
            codigo = codigo,
            tagCliente = tagCliente,
            tipo = tipo,
            cliente = cliente,
            ubicacion = ubicacion,
            marca = marca,
            modelo = modelo,
            numeroSerie = numeroSerie,
            potenciaTension = potenciaTension,
            anio = anio,
            tensiones = tensiones,
            tap = tap,
            conexion = conexion,
            ultimoEstado = EstadoProtocolo.fromLabel(ultimoEstado)
        )
    }

    companion object {
        fun fromDomain(equipo: Equipo): EquipoEntity {
            return EquipoEntity(
                id = equipo.id,
                codigo = equipo.codigo,
                tagCliente = equipo.tagCliente,
                tipo = equipo.tipo,
                cliente = equipo.cliente,
                ubicacion = equipo.ubicacion,
                marca = equipo.marca,
                modelo = equipo.modelo,
                numeroSerie = equipo.numeroSerie,
                potenciaTension = equipo.potenciaTension,
                anio = equipo.anio,
                tensiones = equipo.tensiones,
                tap = equipo.tap,
                conexion = equipo.conexion,
                ultimoEstado = equipo.ultimoEstado.label
            )
        }
    }
}
