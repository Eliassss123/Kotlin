// ============================================================================
// ARCHIVO : model/CodigoRecuperacion.kt
// CAPA    : Modelo / datos
// RESUMEN : representa un código de 6 dígitos generado para recuperar contraseña.
//           Nota: es a la vez modelo y tabla de Room (@Entity), por eso lleva anotaciones de base de datos.
// ============================================================================

package com.induztek.protocolos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// @Entity: esta clase es una TABLA de la base de datos ('codigos_recuperacion'). Cada propiedad es una columna.
@Entity(tableName = "codigos_recuperacion")
// data class: clase pensada para guardar datos; Kotlin genera solo equals, toString y copy().
data class CodigoRecuperacion(
    // Clave primaria (identificador único de la fila). UUID = texto aleatorio único. '= ...' es un valor por defecto.
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    // Correo del usuario al que pertenece el código.
    val correoAsociado: String,
    val codigo: String,
    // Momento de creación en milisegundos. Sirve para saber si el código expiró (10 minutos).
    val timestamp: Long = System.currentTimeMillis(),
    // true cuando el código ya se utilizó (no se puede reusar).
    val usado: Boolean = false
)
