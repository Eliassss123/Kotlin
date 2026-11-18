package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.util.SessionManager
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val email: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

sealed class RecuperacionUiState {
    object Idle : RecuperacionUiState()
    object Loading : RecuperacionUiState()
    object CodigoEnviado : RecuperacionUiState()
    object CodigoVerificado : RecuperacionUiState()
    data class Success(val message: String) : RecuperacionUiState()
    data class Error(val message: String) : RecuperacionUiState()
}

sealed class RegistroUiState {
    object Idle : RegistroUiState()
    object Loading : RegistroUiState()
    data class Success(val message: String) : RegistroUiState()
    data class Error(val message: String) : RegistroUiState()
}

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository? = null,
    private val sessionManager: SessionManager? = null
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _usuarioActivo = MutableStateFlow<UserProfile?>(null)
    val usuarioActivo: StateFlow<UserProfile?> = _usuarioActivo.asStateFlow()

    private val _correoRecuperacion = MutableStateFlow("")
    val correoRecuperacion: StateFlow<String> = _correoRecuperacion.asStateFlow()
    private val _codigoIngresado = MutableStateFlow("")
    val codigoIngresado: StateFlow<String> = _codigoIngresado.asStateFlow()
    private val _nuevaContrasena = MutableStateFlow("")
    val nuevaContrasena: StateFlow<String> = _nuevaContrasena.asStateFlow()
    private val _confirmarContrasena = MutableStateFlow("")
    val confirmarContrasena: StateFlow<String> = _confirmarContrasena.asStateFlow()
    private val _recuperacionState = MutableStateFlow<RecuperacionUiState>(RecuperacionUiState.Idle)
    val recuperacionState: StateFlow<RecuperacionUiState> = _recuperacionState.asStateFlow()
    private val _mensajeExitoLogin = MutableStateFlow<String?>(null)
    val mensajeExitoLogin: StateFlow<String?> = _mensajeExitoLogin.asStateFlow()
    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError.asStateFlow()
    private val _registroState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val registroState: StateFlow<RegistroUiState> = _registroState.asStateFlow()
    private var recoveryCodeVerified = false

    init {
        if (usuarioRepository != null && sessionManager?.estaLogueado() == true) {
            viewModelScope.launch {
                try {
                    val profile = usuarioRepository.getCurrentUser()
                    if (profile.activo) {
                        _usuarioActivo.value = profile
                        _email.value = profile.correo
                        _uiState.value = LoginUiState.Success(profile.correo)
                    } else {
                        sessionManager.cerrarSesion()
                    }
                } catch (exception: Exception) {
                    if (exception is CancellationException) throw exception
                    sessionManager.cerrarSesion()
                    _uiState.value = LoginUiState.Error(errorMessage(exception))
                }
            }
        }
    }

    fun seleccionarPerfilDemo(correoDemo: String, claveDemo: String) {
        _email.value = correoDemo
        _password.value = claveDemo
        _uiState.value = LoginUiState.Idle
    }

    fun cerrarSesion() {
        _usuarioActivo.value = null
        _email.value = ""
        _password.value = ""
        _uiState.value = LoginUiState.Idle
        sessionManager?.cerrarSesion()
    }

    fun actualizarFotoPerfil(fotoPath: String?) {
        val actual = _usuarioActivo.value ?: return
        viewModelScope.launch {
            try {
                val updated = actual.copy(fotoPerfil = fotoPath)
                _usuarioActivo.value = updated
                usuarioRepository?.actualizarFotoPerfil(actual.id, fotoPath)
                if (usuarioRepository != null) _usuarioActivo.value = usuarioRepository.getCurrentUser()
                _profileError.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _usuarioActivo.value = actual
                _profileError.value = errorMessage(exception)
            }
        }
    }

    fun actualizarBiografia(biografia: String?) {
        val actual = _usuarioActivo.value ?: return
        viewModelScope.launch {
            try {
                usuarioRepository?.actualizarBiografia(actual.id, biografia)
                _usuarioActivo.value = actual.copy(biografia = biografia)
                _profileError.value = null
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _profileError.value = errorMessage(exception)
            }
        }
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        _uiState.value = LoginUiState.Idle
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        _uiState.value = LoginUiState.Idle
    }

    fun onCorreoRecuperacionChanged(newCorreo: String) {
        _correoRecuperacion.value = newCorreo
        _recuperacionState.value = RecuperacionUiState.Idle
    }

    fun onCodigoIngresadoChanged(newCodigo: String) {
        _codigoIngresado.value = newCodigo
        _recuperacionState.value = RecuperacionUiState.Idle
    }

    fun onNuevaContrasenaChanged(newPass: String) {
        _nuevaContrasena.value = newPass
        _recuperacionState.value = RecuperacionUiState.Idle
    }

    fun onConfirmarContrasenaChanged(newPass: String) {
        _confirmarContrasena.value = newPass
        _recuperacionState.value = RecuperacionUiState.Idle
    }

    fun resetMensajeExito() {
        _mensajeExitoLogin.value = null
    }

    val isLoginInputValid: Boolean
        get() = validarCorreo(_email.value) && _password.value.isNotBlank()

    fun validarCorreo(correo: String): Boolean =
        correo.trim().matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))

    fun validarContrasena(pass: String): Boolean {
        val value = pass.trim()
        return value.length >= 10 &&
            value.any(Char::isUpperCase) &&
            value.any(Char::isLowerCase) &&
            value.any(Char::isDigit) &&
            value.any { !it.isLetterOrDigit() }
    }

    fun mensajeValidacionContrasena(pass: String): String? {
        val value = pass.trim()
        if (value.length < 10) return "Debe tener al menos 10 caracteres"
        if (!value.any(Char::isUpperCase)) return "Debe incluir al menos una letra mayúscula"
        if (!value.any(Char::isLowerCase)) return "Debe incluir al menos una letra minúscula"
        if (!value.any(Char::isDigit)) return "Debe incluir al menos un número"
        if (!value.any { !it.isLetterOrDigit() }) return "Debe incluir al menos un carácter especial (!@#\$%^&*...)"
        return null
    }

    fun prepararRecuperacion() {
        _correoRecuperacion.value = _email.value
        _codigoIngresado.value = ""
        _nuevaContrasena.value = ""
        _confirmarContrasena.value = ""
        recoveryCodeVerified = false
        _recuperacionState.value = RecuperacionUiState.Idle
    }

    fun solicitarCodigo(correo: String? = null) {
        val targetEmail = (correo ?: _correoRecuperacion.value).trim()
        if (!validarCorreo(targetEmail)) {
            _recuperacionState.value = RecuperacionUiState.Error("Ingresa un correo válido")
            return
        }
        val repository = usuarioRepository
        if (repository == null) {
            _recuperacionState.value = RecuperacionUiState.Error("El servicio de autenticación no está disponible")
            return
        }
        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading
            _correoRecuperacion.value = targetEmail
            recoveryCodeVerified = false
            try {
                repository.solicitarRecuperacion(targetEmail)
                _recuperacionState.value = RecuperacionUiState.CodigoEnviado
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _recuperacionState.value = RecuperacionUiState.Error(errorMessage(exception))
            }
        }
    }

    fun verificarCodigo(codigoIngresado: String? = null) {
        val code = (codigoIngresado ?: _codigoIngresado.value).trim()
        val targetEmail = _correoRecuperacion.value.trim()
        if (!code.matches(Regex("\\d{6}"))) {
            _recuperacionState.value = RecuperacionUiState.Error("Ingresa el código de 6 dígitos")
            return
        }
        val repository = usuarioRepository
        if (repository == null) {
            _recuperacionState.value = RecuperacionUiState.Error("El servicio de autenticación no está disponible")
            return
        }
        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading
            try {
                repository.verificarCodigo(targetEmail, code)
                recoveryCodeVerified = true
                _recuperacionState.value = RecuperacionUiState.CodigoVerificado
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _recuperacionState.value = RecuperacionUiState.Error(errorMessage(exception))
            }
        }
    }

    fun actualizarContrasena(nueva: String? = null) {
        val newPassword = (nueva ?: _nuevaContrasena.value).trim()
        val confirmation = _confirmarContrasena.value.trim()
        val targetEmail = _correoRecuperacion.value.trim()
        val code = _codigoIngresado.value.trim()
        if (!recoveryCodeVerified) {
            _recuperacionState.value = RecuperacionUiState.Error("Primero verifica el código recibido")
            return
        }
        if (!validarContrasena(newPassword)) {
            _recuperacionState.value = RecuperacionUiState.Error(
                mensajeValidacionContrasena(newPassword) ?: "Contraseña inválida"
            )
            return
        }
        if (newPassword != confirmation) {
            _recuperacionState.value = RecuperacionUiState.Error("Las contraseñas no coinciden")
            return
        }
        val repository = usuarioRepository
        if (repository == null) {
            _recuperacionState.value = RecuperacionUiState.Error("El servicio de autenticación no está disponible")
            return
        }
        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading
            try {
                repository.actualizarContrasena(targetEmail, code, newPassword)
                _email.value = targetEmail
                _password.value = newPassword
                _mensajeExitoLogin.value = "Contraseña actualizada, inicia sesión"
                recoveryCodeVerified = false
                _recuperacionState.value = RecuperacionUiState.Success("Contraseña actualizada con éxito")
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _recuperacionState.value = RecuperacionUiState.Error(errorMessage(exception))
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    fun registrar(nombre: String, correo: String, contrasena: String, aceptaTerminos: Boolean) {
        val name = nombre.trim()
        val emailValue = correo.trim()
        if (name.isBlank()) {
            _registroState.value = RegistroUiState.Error("Ingresa tu nombre")
            return
        }
        if (!validarCorreo(emailValue)) {
            _registroState.value = RegistroUiState.Error("Ingresa un correo válido")
            return
        }
        if (!validarContrasena(contrasena)) {
            _registroState.value = RegistroUiState.Error(
                mensajeValidacionContrasena(contrasena) ?: "Contraseña inválida"
            )
            return
        }
        if (!aceptaTerminos) {
            _registroState.value = RegistroUiState.Error("Debes aceptar los términos y condiciones")
            return
        }
        val repository = usuarioRepository
        if (repository == null) {
            _registroState.value = RegistroUiState.Error("El servicio de autenticación no está disponible")
            return
        }
        viewModelScope.launch {
            _registroState.value = RegistroUiState.Loading
            try {
                val profile = repository.register(name, emailValue, contrasena, termsAccepted = true)
                val message = if (profile.activo) {
                    "Cuenta creada. Ya puedes iniciar sesión."
                } else {
                    "Solicitud recibida. Un administrador debe aprobar la cuenta antes de iniciar sesión."
                }
                _mensajeExitoLogin.value = message
                _registroState.value = RegistroUiState.Success(message)
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _registroState.value = RegistroUiState.Error(errorMessage(exception))
            }
        }
    }

    fun resetRegistroState() {
        _registroState.value = RegistroUiState.Idle
    }

    fun login() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value
        if (!validarCorreo(emailValue)) {
            _uiState.value = LoginUiState.Error("Ingresa un correo válido")
            return
        }
        if (passwordValue.isBlank()) {
            _uiState.value = LoginUiState.Error("Ingresa tu contraseña")
            return
        }
        val repository = usuarioRepository
        if (repository == null) {
            _uiState.value = LoginUiState.Error("El servicio de autenticación no está disponible")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val session = repository.login(emailValue, passwordValue)
                if (!session.profile.activo) {
                    _uiState.value = LoginUiState.Error("Cuenta desactivada. Contacte al Jefe Técnico de Induztek.")
                    return@launch
                }
                sessionManager?.guardarSesion(
                    session.profile.correo,
                    session.profile.perfilCodigo,
                    session.profile.nombre,
                    session.token
                )
                _usuarioActivo.value = session.profile
                _uiState.value = LoginUiState.Success(session.profile.correo)
            } catch (exception: Exception) {
                if (exception is CancellationException) throw exception
                _uiState.value = LoginUiState.Error(errorMessage(exception))
            }
        }
    }

    private fun errorMessage(exception: Exception): String = when (exception) {
        is HttpException -> when (exception.code()) {
            400 -> "Los datos enviados no son válidos"
            401 -> "Correo o contraseña incorrectos"
            403 -> "La cuenta está pendiente de aprobación o fue desactivada"
            404 -> "No se encontró el recurso solicitado"
            409 -> "El registro ya existe"
            else -> "Error del servidor (${exception.code()})"
        }
        is IOException -> "No fue posible conectar con el servidor. Verifica tu conexión."
        else -> exception.localizedMessage ?: "Ocurrió un error inesperado"
    }
}
