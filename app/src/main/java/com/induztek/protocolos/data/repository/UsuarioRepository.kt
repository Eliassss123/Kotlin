package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.ActiveRequest
import com.induztek.protocolos.data.remote.AuthApi
import com.induztek.protocolos.data.remote.LoginRequest
import com.induztek.protocolos.data.remote.RecoveryRequest
import com.induztek.protocolos.data.remote.RegisterRequest
import com.induztek.protocolos.data.remote.ResetPasswordRequest
import com.induztek.protocolos.data.remote.UserProfileUpdateRequest
import com.induztek.protocolos.data.remote.VerifyResetCodeRequest
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.requireSuccess
import com.induztek.protocolos.data.remote.toProfile
import com.induztek.protocolos.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginSession(val token: String, val profile: UserProfile)

class UsuarioRepository(
    private val api: AuthApi,
    private val mediaRepository: MediaRepository
) {
    private val _allUsuarios = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsuarios: StateFlow<List<UserProfile>> = _allUsuarios.asStateFlow()

    suspend fun login(email: String, password: String): LoginSession {
        val result = api.login(LoginRequest(email, password)).requireBody()
        return LoginSession(
            result.token,
            UserProfile(
                id = result.id,
                correo = result.email,
                nombre = result.usuario,
                perfilCodigo = result.perfilCodigo,
                empresaCliente = result.empresaCliente,
                activo = result.activo
            )
        )
    }

    suspend fun register(name: String, email: String, password: String, termsAccepted: Boolean): UserProfile =
        api.register(
            RegisterRequest(
                email = email,
                password = password,
                name = name,
                termsAccepted = termsAccepted
            )
        ).requireBody().toProfile()

    suspend fun getCurrentUser(): UserProfile = api.getCurrentUser().requireBody().toProfile()

    suspend fun seedInitialDataIfEmpty() {
        refresh()
    }

    suspend fun refresh() {
        _allUsuarios.value = api.getUsers().requireBody().map { it.toProfile() }
    }

    suspend fun actualizarFotoPerfil(id: String, fotoPath: String?) {
        val current = getCurrentUser()
        require(current.id == id) { "The active account changed while updating its profile" }
        updateProfile(
            photoUrl = mediaRepository.uploadIfLocal(fotoPath),
            biography = current.biografia
        )
    }

    suspend fun actualizarBiografia(id: String, biografia: String?) {
        val current = getCurrentUser()
        require(current.id == id) { "The active account changed while updating its profile" }
        updateProfile(photoUrl = current.fotoPerfil, biography = biografia)
    }

    private suspend fun updateProfile(photoUrl: String?, biography: String?) {
        val response = api.updateProfile(
            UserProfileUpdateRequest(photoUrl = photoUrl, biography = biography)
        ).requireBody().toProfile()
        _allUsuarios.value = _allUsuarios.value.map { if (it.id == response.id) response else it }
    }

    suspend fun setActivo(id: String, activo: Boolean) {
        val updated = api.setUserActive(id, ActiveRequest(active = activo)).requireBody().toProfile()
        _allUsuarios.value = _allUsuarios.value.map { if (it.id == updated.id) updated else it }
    }

    suspend fun solicitarRecuperacion(email: String) {
        api.requestPasswordRecovery(RecoveryRequest(email)).requireBody()
    }

    suspend fun verificarCodigo(email: String, code: String) {
        api.verifyRecoveryCode(VerifyResetCodeRequest(email, code)).requireSuccess()
    }

    suspend fun actualizarContrasena(email: String, code: String, nueva: String) {
        api.resetPassword(ResetPasswordRequest(email, code, nueva)).requireBody()
    }
}
