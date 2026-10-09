// ============================================================================
// ARCHIVO : viewmodel/LoginViewModel.kt
// CAPA    : ViewModel (MVVM) - LÓGICA DE NEGOCIO de autenticación
// RESUMEN : Maneja autenticación, perfiles P-01 a P-04, verificación de usuario activo (RF-22)
//           y flujo de recuperación de contraseñas.
// ============================================================================

package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.model.CodigoRecuperacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

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

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository? = null
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Usuario autenticado actualmente con su perfil (P-01 a P-04)
    private val _usuarioActivo = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActivo: StateFlow<UsuarioEntity?> = _usuarioActivo.asStateFlow()

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
    }

    private val _correoRecuperacion = MutableStateFlow("")
    val correoRecuperacion: StateFlow<String> = _correoRecuperacion.asStateFlow()

    private val _codigoIngresado = MutableStateFlow("")
    val codigoIngresado: StateFlow<String> = _codigoIngresado.asStateFlow()

    private val _codigoGenerado = MutableStateFlow<String?>(null)
    val codigoGenerado: StateFlow<String?> = _codigoGenerado.asStateFlow()

    private val _mostrarDialogCodigo = MutableStateFlow(false)
    val mostrarDialogCodigo: StateFlow<Boolean> = _mostrarDialogCodigo.asStateFlow()

    private val _nuevaContrasena = MutableStateFlow("")
    val nuevaContrasena: StateFlow<String> = _nuevaContrasena.asStateFlow()

    private val _confirmarContrasena = MutableStateFlow("")
    val confirmarContrasena: StateFlow<String> = _confirmarContrasena.asStateFlow()

    private val _recuperacionState = MutableStateFlow<RecuperacionUiState>(RecuperacionUiState.Idle)
    val recuperacionState: StateFlow<RecuperacionUiState> = _recuperacionState.asStateFlow()

    private val _mensajeExitoLogin = MutableStateFlow<String?>(null)
    val mensajeExitoLogin: StateFlow<String?> = _mensajeExitoLogin.asStateFlow()

    init {
        if (usuarioRepository != null) {
            viewModelScope.launch {
                usuarioRepository.seedInitialDataIfEmpty()
            }
        }
    }

    fun validarCorreo(correo: String): Boolean {
        if (correo.isBlank()) return false
        return try {
            android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
        } catch (e: Throwable) {
            val pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
            correo.trim().matches(pattern.toRegex())
        }
    }

    fun validarContrasena(pass: String): Boolean {
        val p = pass.trim()
        if (p.length < 8) return false
        if (!p.any { it.isUpperCase() }) return false
        if (!p.any { it.isLowerCase() }) return false
        if (!p.any { it.isDigit() }) return false
        if (!p.any { !it.isLetterOrDigit() }) return false
        return true
    }

    fun mensajeValidacionContrasena(pass: String): String? {
        val p = pass.trim()
        if (p.isEmpty()) return null
        if (p.length < 8) return "Debe tener al menos 8 caracteres"
        if (!p.any { it.isUpperCase() }) return "Debe incluir al menos una letra mayúscula"
        if (!p.any { it.isLowerCase() }) return "Debe incluir al menos una letra minúscula"
        if (!p.any { it.isDigit() }) return "Debe incluir al menos un número"
        if (!p.any { !it.isLetterOrDigit() }) return "Debe incluir al menos un carácter especial (!@#\$%^&*...)"
        return null
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

    fun cerrarDialogCodigo() {
        _mostrarDialogCodigo.value = false
    }

    fun resetMensajeExito() {
        _mensajeExitoLogin.value = null
    }

    val isLoginInputValid: Boolean
        get() = validarCorreo(_email.value) && validarContrasena(_password.value)

    fun login() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value.trim()

        if (!validarCorreo(emailValue)) {
            _uiState.value = LoginUiState.Error("Ingresa un correo válido")
            return
        }

        if (!validarContrasena(passwordValue)) {
            _uiState.value = LoginUiState.Error(mensajeValidacionContrasena(passwordValue) ?: "Contraseña inválida")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(400)

            if (usuarioRepository != null) {
                usuarioRepository.seedInitialDataIfEmpty()
                val usuario = usuarioRepository.buscarPorCorreo(emailValue)
                if (usuario != null) {
                    // RF-22: El personal desactivado no puede iniciar sesión
                    if (!usuario.activo) {
                        _uiState.value = LoginUiState.Error("Cuenta desactivada. Contacte al Jefe Técnico de Induztek.")
                        return@launch
                    }

                    if (usuario.contrasena == passwordValue) {
                        _usuarioActivo.value = usuario
                        _uiState.value = LoginUiState.Success(emailValue)
                    } else {
                        _uiState.value = LoginUiState.Error("Contraseña incorrecta")
                    }
                } else {
                    _uiState.value = LoginUiState.Error("El correo no está registrado en el sistema")
                }
            } else {
                val fallbackUser = UsuarioEntity(
                    id = "USR-DEMO",
                    correo = emailValue,
                    contrasena = passwordValue,
                    nombre = "Usuario Demo",
                    perfilCodigo = "P-01",
                    activo = true
                )
                _usuarioActivo.value = fallbackUser
                _uiState.value = LoginUiState.Success(emailValue)
            }
        }
    }

    fun prepararRecuperacion() {
        _correoRecuperacion.value = _email.value
        _codigoIngresado.value = ""
        _codigoGenerado.value = null
        _nuevaContrasena.value = ""
        _confirmarContrasena.value = ""
        _recuperacionState.value = RecuperacionUiState.Idle
        _mostrarDialogCodigo.value = false
    }

    fun solicitarCodigo(correo: String? = null) {
        val emailAUsar = (correo ?: _correoRecuperacion.value).trim()
        if (!validarCorreo(emailAUsar)) {
            _recuperacionState.value = RecuperacionUiState.Error("Ingresa un correo válido")
            return
        }

        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading
            _correoRecuperacion.value = emailAUsar

            val usuario = usuarioRepository?.buscarPorCorreo(emailAUsar)
            if (usuarioRepository != null && usuario == null) {
                _recuperacionState.value = RecuperacionUiState.Error("El correo no está registrado en el sistema")
                return@launch
            }

            val codigo = Random.nextInt(100000, 999999).toString()
            _codigoGenerado.value = codigo

            val codigoRecuperacion = CodigoRecuperacion(
                correoAsociado = emailAUsar,
                codigo = codigo,
                timestamp = System.currentTimeMillis(),
                usado = false
            )
            usuarioRepository?.guardarCodigoRecuperacion(codigoRecuperacion)

            _mostrarDialogCodigo.value = true
            _recuperacionState.value = RecuperacionUiState.CodigoEnviado
        }
    }

    fun verificarCodigo(codigoIngresado: String? = null) {
        val codigoTarget = (codigoIngresado ?: _codigoIngresado.value).trim()
        val emailTarget = _correoRecuperacion.value.trim()

        if (codigoTarget.length < 6) {
            _recuperacionState.value = RecuperacionUiState.Error("Ingresa el código de 6 dígitos")
            return
        }

        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading
            delay(300)

            if (usuarioRepository != null) {
                val ultimoCodigo = usuarioRepository.obtenerUltimoCodigo(emailTarget)
                val diezMinutosEnMs = 10 * 60 * 1000L

                if (ultimoCodigo != null &&
                    ultimoCodigo.codigo == codigoTarget &&
                    !ultimoCodigo.usado &&
                    (System.currentTimeMillis() - ultimoCodigo.timestamp) <= diezMinutosEnMs
                ) {
                    _recuperacionState.value = RecuperacionUiState.CodigoVerificado
                } else {
                    _recuperacionState.value = RecuperacionUiState.Error("Código incorrecto o expirado")
                }
            } else {
                if (codigoTarget == _codigoGenerado.value) {
                    _recuperacionState.value = RecuperacionUiState.CodigoVerificado
                } else {
                    _recuperacionState.value = RecuperacionUiState.Error("Código incorrecto o expirado")
                }
            }
        }
    }

    fun actualizarContrasena(nueva: String? = null) {
        val nuevaPass = (nueva ?: _nuevaContrasena.value).trim()
        val confirmPass = _confirmarContrasena.value.trim()
        val emailTarget = _correoRecuperacion.value.trim()

        if (!validarContrasena(nuevaPass)) {
            _recuperacionState.value = RecuperacionUiState.Error(mensajeValidacionContrasena(nuevaPass) ?: "Contraseña inválida")
            return
        }

        if (nuevaPass != confirmPass) {
            _recuperacionState.value = RecuperacionUiState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _recuperacionState.value = RecuperacionUiState.Loading

            val exito = usuarioRepository?.actualizarContrasena(emailTarget, nuevaPass) ?: true
            if (exito) {
                val ultimoCodigo = usuarioRepository?.obtenerUltimoCodigo(emailTarget)
                if (ultimoCodigo != null) {
                    usuarioRepository?.marcarCodigoComoUsado(ultimoCodigo.id)
                }

                _email.value = emailTarget
                _password.value = nuevaPass
                _mensajeExitoLogin.value = "Contraseña actualizada, inicia sesión"
                _recuperacionState.value = RecuperacionUiState.Success("Contraseña actualizada con éxito")
            } else {
                _recuperacionState.value = RecuperacionUiState.Error("Error al actualizar la contraseña")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
