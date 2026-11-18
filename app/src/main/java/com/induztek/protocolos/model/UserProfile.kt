package com.induztek.protocolos.model

data class UserProfile(
    val id: String,
    val correo: String,
    val nombre: String,
    val perfilCodigo: String,
    val empresaCliente: String? = null,
    val activo: Boolean = true,
    val fotoPerfil: String? = null,
    val biografia: String? = null
) {
    fun getPerfil(): PerfilUsuario = PerfilUsuario.fromCodigo(perfilCodigo)
}
