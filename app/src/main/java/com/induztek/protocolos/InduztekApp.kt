package com.induztek.protocolos

import android.app.Application
import com.induztek.protocolos.data.local.AppDatabase
import com.induztek.protocolos.data.repository.*

class InduztekApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val equipoRepository by lazy { EquipoRepository(database.equipoDao()) }
    val pruebaRepository by lazy { PruebaRepository(database.pruebaDao()) }
    val protocoloRepository by lazy { ProtocoloRepository(database.protocoloDao()) }
    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao(), database.codigoRecuperacionDao()) }
    val visitaRepository by lazy { VisitaRepository(database.visitaDao()) }
    val actaRepository by lazy { ActaRepository(database.actaDao()) }
    val instrumentoRepository by lazy { InstrumentoRepository(database.instrumentoDao()) }
    val sessionManager by lazy { com.induztek.protocolos.util.SessionManager(this) }
}
