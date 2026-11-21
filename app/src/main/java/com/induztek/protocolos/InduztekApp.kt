package com.induztek.protocolos

import android.app.Application
import com.induztek.protocolos.data.remote.RetrofitClient
import com.induztek.protocolos.data.repository.*

class InduztekApp : Application() {

    val sessionManager by lazy { com.induztek.protocolos.util.SessionManager(this) }
    private val mediaRepository by lazy {
        MediaRepository(RetrofitClient.mediaApi, filesDir)
    }
    val equipoRepository by lazy { EquipoRepository(RetrofitClient.catalogApi) }
    val pruebaRepository by lazy { PruebaRepository(RetrofitClient.operationsApi) }
    val protocoloRepository by lazy {
        ProtocoloRepository(RetrofitClient.operationsApi, mediaRepository)
    }
    val usuarioRepository by lazy {
        UsuarioRepository(RetrofitClient.authApi, mediaRepository)
    }
    val visitaRepository by lazy { VisitaRepository(RetrofitClient.operationsApi) }
    val actaRepository by lazy { ActaRepository(RetrofitClient.operationsApi) }
    val instrumentoRepository by lazy { InstrumentoRepository(RetrofitClient.catalogApi) }

    override fun onCreate() {
        super.onCreate()
        com.induztek.protocolos.util.NotificationHelper.crearCanalesNotificacion(this)
        RetrofitClient.configureAccessTokenProvider(sessionManager::obtenerToken)
    }
}
