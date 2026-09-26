package com.induztek.app.domain.repository

import com.induztek.app.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato de Autenticación y Gestión de Sesión.
 */
interface AuthRepository {
    val currentUsuario: StateFlow<Usuario?>
    suspend fun login(email: String, password: String): Result<Usuario>
    suspend fun logout()
    suspend fun getUsuarioActual(): Usuario?
}
