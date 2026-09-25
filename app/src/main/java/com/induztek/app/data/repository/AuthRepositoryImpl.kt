package com.induztek.app.data.repository

import com.induztek.app.domain.model.RolUsuario
import com.induztek.app.domain.model.Usuario
import com.induztek.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de autenticación con usuarios sintéticos corporativos
 * para entorno offline-first y desarrollo de MVP.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _currentUsuario = MutableStateFlow<Usuario?>(null)
    override val currentUsuario: StateFlow<Usuario?> = _currentUsuario.asStateFlow()

    // Cuentas sintéticas de prueba (cumplen con PasswordPolicy: >=8 chars, Mayús, Minús, Número, Especial)
    private val usuariosRegistrados = listOf(
        Pair("tecnico@induztek.cl", "Induztek2026!") to Usuario(
            id = 1L,
            email = "tecnico@induztek.cl",
            nombre = "Ing. Carlos Mendoza",
            rol = RolUsuario.TECNICO,
            token = "jwt_mock_tecnico_token_123"
        ),
        Pair("admin@induztek.cl", "Admin2026!") to Usuario(
            id = 2L,
            email = "admin@induztek.cl",
            nombre = "Supervisora Elena Rivas",
            rol = RolUsuario.ADMIN,
            token = "jwt_mock_admin_token_456"
        )
    )

    override suspend fun login(email: String, password: String): Result<Usuario> {
        val match = usuariosRegistrados.firstOrNull { (cred, _) ->
            cred.first.equals(email, ignoreCase = true) && cred.second == password
        }

        return if (match != null) {
            val user = match.second
            _currentUsuario.value = user
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales incorrectas. Verifica tu correo y contraseña."))
        }
    }

    override suspend fun logout() {
        _currentUsuario.value = null
    }

    override suspend fun getUsuarioActual(): Usuario? {
        return _currentUsuario.value
    }
}
