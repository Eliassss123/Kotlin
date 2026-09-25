package com.induztek.app.presentation.screen.login.state

import com.induztek.app.domain.model.RolUsuario

/**
 * Estado inmutable de la pantalla de Login.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: List<String> = emptyList()
)

/**
 * Efectos puntuales del Login (navegación y notificaciones).
 */
sealed interface LoginEffect {
    data class NavigateToHome(val rol: RolUsuario) : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}
