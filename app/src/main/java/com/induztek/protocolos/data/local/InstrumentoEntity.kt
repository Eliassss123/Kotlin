






package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.Instrumento


@Entity(tableName = "instrumentos")
data class InstrumentoEntity(
    @PrimaryKey val id: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val numeroSerie: String,
    val vencimientoCalibracion: String,
    val activo: Boolean = true
) {
    
    fun toDomain(): Instrumento {
        return Instrumento(
            id = id,
            tipo = tipo,
            marca = marca,
            modelo = modelo,
            numeroSerie = numeroSerie,
            vencimientoCalibracion = vencimientoCalibracion,
            activo = activo
        )
    }

    companion object {
        
        fun fromDomain(instrumento: Instrumento): InstrumentoEntity {
            return InstrumentoEntity(
                id = instrumento.id,
                tipo = instrumento.tipo,
                marca = instrumento.marca,
                modelo = instrumento.modelo,
                numeroSerie = instrumento.numeroSerie,
                vencimientoCalibracion = instrumento.vencimientoCalibracion,
                activo = instrumento.activo
            )
        }
    }
}
