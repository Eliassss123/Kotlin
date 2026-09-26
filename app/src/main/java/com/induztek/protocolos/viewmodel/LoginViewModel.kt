package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val email: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("el.farr@duocuc.cl")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("Induztek2026!")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    val isInputValid: Boolean
        get() = _email.value.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value.trim()).matches() &&
                _password.value.length >= 6

    fun login() {
        if (!isInputValid) {
            _uiState.value = LoginUiState.Error("Por favor ingrese un correo válido y clave de al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(1000) // Simular autenticación de red con backend
            if (_email.value.trim().lowercase() == "el.farr@duocuc.cl" || _email.value.contains("@")) {
                _uiState.value = LoginUiState.Success(_email.value)
            } else {
                _uiState.value = LoginUiState.Error("Credenciales inválidas")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
