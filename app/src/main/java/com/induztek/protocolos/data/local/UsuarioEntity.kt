// ============================================================================
// ARCHIVO : data/local/UsuarioEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : la TABLA 'usuarios'.
//           Aviso de seguridad: 'contrasena' se guarda en TEXTO PLANO. Sirve para un MVP/demo; en una app real se
//             guardaría un hash.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// @Entity: tabla 'usuarios'.
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    // Si no se indica id, se genera uno aleatorio (UUID).
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val correo: String,
    // Contraseña en texto plano (ver aviso arriba).
    val contrasena: String,
    // Valor por defecto si no se entrega nombre.
    val nombre: String = "Usuario Induztek"
)
