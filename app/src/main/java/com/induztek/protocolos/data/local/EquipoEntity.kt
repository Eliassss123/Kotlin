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

/**
 * Entidad de Room que representa la tabla 'equipos' en la base de datos local.
 * Almacena especificaciones técnicas de placa, ubicación, cliente y estado del último protocolo.
 */
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
    val tap: String = "3",
    val conexion: String = "Dyn11",
    val ultimoEstado: String
) {
    /**
     * Convierte la entidad de base de datos [EquipoEntity] al modelo de dominio [Equipo].
     */
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
        /**
         * Crea una instancia de [EquipoEntity] a partir del modelo de dominio [Equipo].
         */
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
