package com.induztek.app.domain.model

/**
 * Roles de usuario dentro del sistema Induztek Ingeniería.
 */
enum class RolUsuario {
    TECNICO,   // Ingeniero/Técnico en terreno: registra pruebas, consulta historial
    ADMIN      // Supervisor/Jefe de operaciones: formaliza protocolos, gestiona equipos
}

/**
 * Modelo de dominio de Usuario autenticado.
 */
data class Usuario(
    val id: Long = 0,
    val email: String,
    val nombre: String,
    val rol: RolUsuario,
    val token: String? = null
)
