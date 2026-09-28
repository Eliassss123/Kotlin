// ============================================================================
// ARCHIVO : viewmodel/LoginViewModel.kt
// CAPA    : ViewModel (MVVM) - LÓGICA DE NEGOCIO de autenticación
// RESUMEN : el archivo con más lógica. Se encarga de:
//           1) validar correo y contraseña,  2) iniciar sesión,
//           3) recuperar contraseña (pedir código -> verificarlo -> cambiar contraseña).
//           Lo usan 4 pantallas: Login, OlvideContrasena, VerificarCodigo y NuevaContrasena.
// ============================================================================

package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.UsuarioRepository
import com.induztek.protocolos.model.CodigoRecuperacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// sealed class = conjunto CERRADO de estados posibles de la pantalla de login:
// Idle (reposo), Loading (cargando), Success (éxito, con el correo) y Error (con mensaje).
// Es como un enum, pero cada estado puede llevar datos. La UI decide qué dibujar según el estado actual.
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val email: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

// Lo mismo, pero para el flujo 'olvidé mi contraseña': CodigoEnviado -> CodigoVerificado -> Success.
sealed class RecuperacionUiState {
    object Idle : RecuperacionUiState()
    object Loading : RecuperacionUiState()
    object CodigoEnviado : RecuperacionUiState()
    object CodigoVerificado : RecuperacionUiState()
    data class Success(val message: String) : RecuperacionUiState()
    data class Error(val message: String) : RecuperacionUiState()
}

// 'usuarioRepository' es nullable (UsuarioRepository?) con valor por defecto null: así los tests unitarios
// pueden crear LoginViewModel() sin necesitar una base de datos.
class LoginViewModel(
    private val usuarioRepository: UsuarioRepository? = null
) : ViewModel() {

    // --- Login State ---
    // Estado del formulario de login. Patrón _privado (modificable aquí) / público (solo lectura para la UI).
    // Cada MutableStateFlow guarda UN valor que Compose observa.
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // --- Password Recovery State ---
    // Estados del flujo de recuperación de contraseña (correo, código escrito, código generado, diálogo,
    //   contraseñas).
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

    // Mensaje verde que se muestra en el login tras cambiar la contraseña (null = no mostrar).
    private val _mensajeExitoLogin = MutableStateFlow<String?>(null)
    val mensajeExitoLogin: StateFlow<String?> = _mensajeExitoLogin.asStateFlow()

    // Al crearse, siembra los usuarios de ejemplo (solo si hay repositorio).
    init {
        if (usuarioRepository != null) {
            viewModelScope.launch {
                usuarioRepository.seedInitialDataIfEmpty()
            }
        }
    }

    // --- Validation Helper Functions ---
    // LÓGICA: valida el formato del correo. Usa el validador de Android (Patterns.EMAIL_ADDRESS).
    // El try/catch existe porque en los tests unitarios las clases de Android no están disponibles; entonces usa un
    //   plan B con regex.
    fun validarCorreo(correo: String): Boolean {
        if (correo.isBlank()) return false
        return try {
            android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
        } catch (e: Throwable) {
            val pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
            correo.trim().matches(pattern.toRegex())
        }
    }

    /**
     * Valida que la contraseña cumpla:
     * - Mínimo 8 caracteres
     * - Al menos una letra mayúscula
     * - Al menos una letra minúscula
     * - Al menos un número
     * - Al menos un carácter especial (!@#$%^&*-._ etc.)
     */
    // LÓGICA: reglas de contraseña. Devuelve true solo si cumple TODO: 8+ caracteres, mayúscula, minúscula, número y
    //         un símbolo.
    fun validarContrasena(pass: String): Boolean {
        val p = pass.trim()
        if (p.length < 8) return false
        if (!p.any { it.isUpperCase() }) return false
        if (!p.any { it.isLowerCase() }) return false
        if (!p.any { it.isDigit() }) return false
        if (!p.any { !it.isLetterOrDigit() }) return false
        return true
    }

    // Devuelve el mensaje de la PRIMERA regla que falla, o null si todo está bien ('String?' = puede ser null).
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

    // --- Value Setters ---
    // Eventos de la UI: reciben lo que escribió el usuario y actualizan el estado. Al escribir se vuelve a Idle para
    //   borrar errores viejos.
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

    // Propiedad calculada (get()): se recalcula cada vez que se lee.
    val isLoginInputValid: Boolean
        get() = validarCorreo(_email.value) && validarContrasena(_password.value)

    // --- Authentication Actions ---
    // LÓGICA DE NEGOCIO: iniciar sesión. Pasos: validar formato -> buscar usuario en la base -> comparar contraseña.
    // Cada resultado se publica como un LoginUiState distinto.
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
            // delay = pausa de medio segundo (simula el tiempo de una llamada a un servidor). Solo dentro de una
            //   corrutina.
            delay(500) // Simulated loading

            if (usuarioRepository != null) {
                // Consulta a la base de datos (Room) mediante el repositorio.
                val usuario = usuarioRepository.buscarPorCorreo(emailValue)
                // Compara en texto plano (en una app real se compararía un hash). Tres resultados: éxito, correo no
                //   registrado o contraseña incorrecta.
                if (usuario != null && usuario.contrasena == passwordValue) {
                    _uiState.value = LoginUiState.Success(emailValue)
                } else if (usuario == null) {
                    _uiState.value = LoginUiState.Error("El correo no está registrado")
                } else {
                    _uiState.value = LoginUiState.Error("Contraseña incorrecta")
                }
            } else {
                // Si no hay repositorio (caso de pruebas) se acepta el login.
                // Fallback if no repository provided
                _uiState.value = LoginUiState.Success(emailValue)
            }
        }
    }

    // --- Password Recovery Actions ---
    // Reinicia todos los campos del flujo de recuperación. Se llama al tocar '¿Olvidaste tu contraseña?'.
    fun prepararRecuperacion() {
        _correoRecuperacion.value = _email.value
        _codigoIngresado.value = ""
        _codigoGenerado.value = null
        _nuevaContrasena.value = ""
        _confirmarContrasena.value = ""
        _recuperacionState.value = RecuperacionUiState.Idle
        _mostrarDialogCodigo.value = false
    }

    // PASO 1 de la recuperación: valida el correo, comprueba que exista y genera un código.
    // Como no hay servidor de correo, el código se muestra en un diálogo (simulación).
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

            // Código aleatorio de 6 dígitos.
            val codigo = Random.nextInt(100000, 999999).toString()
            _codigoGenerado.value = codigo

            // Se guarda en la base con la hora actual para poder verificar después que no haya expirado.
            val codigoRecuperacion = CodigoRecuperacion(
                correoAsociado = emailAUsar,
                codigo = codigo,
                timestamp = System.currentTimeMillis(),
                usado = false
            )
            usuarioRepository?.guardarCodigoRecuperacion(codigoRecuperacion)

            // Ordena a la pantalla mostrar el diálogo con el código.
            _mostrarDialogCodigo.value = true
            _recuperacionState.value = RecuperacionUiState.CodigoEnviado
        }
    }

    // PASO 2: comprueba que el código escrito sea correcto.
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
                // 10 minutos * 60 segundos * 1000 milisegundos.
                val diezMinutosEnMs = 10 * 60 * 1000L

                // REGLAS: el código debe existir, coincidir, no estar usado y tener menos de 10 minutos. Si falla
                //         cualquiera -> error.
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
                // Plan B sin base de datos: compara con el código guardado en memoria.
                // Fallback check against in-memory generated code
                if (codigoTarget == _codigoGenerado.value) {
                    _recuperacionState.value = RecuperacionUiState.CodigoVerificado
                } else {
                    _recuperacionState.value = RecuperacionUiState.Error("Código incorrecto o expirado")
                }
            }
        }
    }

    // PASO 3: valida la nueva contraseña, exige que coincida con la confirmación y la guarda.
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

            // '?.' = llamada segura (si no hay repositorio, no falla). '?: true' = en ese caso se asume éxito.
            val exito = usuarioRepository?.actualizarContrasena(emailTarget, nuevaPass) ?: true
            if (exito) {
                val ultimoCodigo = usuarioRepository?.obtenerUltimoCodigo(emailTarget)
                if (ultimoCodigo != null) {
                    usuarioRepository?.marcarCodigoComoUsado(ultimoCodigo.id)
                }

                _email.value = emailTarget
                _password.value = nuevaPass
                // Deja listo el mensaje verde y los datos para que el login se rellene solo al volver.
                _mensajeExitoLogin.value = "Contraseña actualizada, inicia sesión"
                _recuperacionState.value = RecuperacionUiState.Success("Contraseña actualizada con éxito")
            } else {
                _recuperacionState.value = RecuperacionUiState.Error("Error al actualizar la contraseña")
            }
        }
    }

    // Vuelve el login al estado de reposo.
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
