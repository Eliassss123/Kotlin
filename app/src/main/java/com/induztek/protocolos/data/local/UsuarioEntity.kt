






package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.induztek.protocolos.model.PerfilUsuario
import java.util.UUID


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
    
    fun getPerfil(): PerfilUsuario = PerfilUsuario.fromCodigo(perfilCodigo)
}
