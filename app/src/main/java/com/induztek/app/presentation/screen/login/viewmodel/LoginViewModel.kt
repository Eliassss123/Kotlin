package com.induztek.app.presentation.screen.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.app.domain.usecase.ValidarLoginUseCase
import com.induztek.app.presentation.screen.login.state.LoginEffect
import com.induztek.app.presentation.screen.login.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Login.
 *
 * Cumple con la restricción de NO manejar Android Context directamente.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validarLoginUseCase: ValidarLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>()
    val effects: SharedFlow<LoginEffect> = _effects.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    /**
     * Facilidad para el evaluador/docente: autocompletar credenciales de prueba.
     */
    fun onQuickFill(email: String, pass: String) {
        _uiState.update {
            it.copy(
                email = email,
                password = pass,
                errorMessage = null
            )
        }
    }

    fun login() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = validarLoginUseCase(state.email, state.password)
            result.fold(
                onSuccess = { usuario ->
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(LoginEffect.NavigateToHome(usuario.rol))
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Error al iniciar sesión"
                        )
                    }
                }
            )
        }
    }
}
