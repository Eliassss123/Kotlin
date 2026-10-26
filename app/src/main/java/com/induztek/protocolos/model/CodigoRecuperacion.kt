






package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "codigos_recuperacion")

data class CodigoRecuperacion(
    
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    
    val correoAsociado: String,
    val codigo: String,
    
    val timestamp: Long = System.currentTimeMillis(),
    
    val usado: Boolean = false
)
