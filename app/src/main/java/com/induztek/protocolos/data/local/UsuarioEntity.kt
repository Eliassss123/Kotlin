// ============================================================================
// ARCHIVO : data/local/UsuarioEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'usuarios' con perfiles P-01 a P-04 y estado activo/desactivado (RF-22):
//           - El personal desactivado no inicia sesión pero conserva su nombre en protocolos.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.PerfilUsuario
import java.util.UUID

/**
 * Entidad de Room que representa la tabla 'usuarios' en la base de datos local.
 * Almacena credenciales, perfiles (P-01 a P-04), estado de activación (RF-22) y datos de perfil de usuario.
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val correo: String,
    val contrasena: String,
    val nombre: String = "Usuario Induztek",
    val perfilCodigo: String = PerfilUsuario.TECNICO_TERRENO.codigo,
    val empresaCliente: String? = null,
    val activo: Boolean = true,
    val fotoPerfil: String? = null,
    val biografia: String? = null
) {
    /**
     * Retorna el [PerfilUsuario] asociado al código de perfil del usuario.
     */
    fun getPerfil(): PerfilUsuario = PerfilUsuario.fromCodigo(perfilCodigo)
}
