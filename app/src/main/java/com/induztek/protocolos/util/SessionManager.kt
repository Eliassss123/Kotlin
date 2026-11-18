package com.induztek.protocolos.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "induztek_session_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    fun guardarSesion(correo: String, rol: String, nombre: String, token: String? = null) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, correo)
            .putString(KEY_USER_ROLE, rol)
            .putString(KEY_USER_NAME, nombre)
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }

    fun estaLogueado(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun obtenerCorreoSesion(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun obtenerRolSesion(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun obtenerNombreSesion(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun obtenerToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
