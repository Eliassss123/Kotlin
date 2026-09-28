// ============================================================================
// ARCHIVO : InduztekApp.kt
// CAPA    : arranque de la app
// RESUMEN : la clase Application se crea UNA sola vez, antes que cualquier pantalla.
//           Funciona como "caja de herramientas" compartida: aquí se crean la base de datos
//           y los repositorios una única vez. Está registrada en AndroidManifest.xml (android:name=".InduztekApp").
// ============================================================================

package com.induztek.protocolos

import android.app.Application
import com.induztek.protocolos.data.local.AppDatabase
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.ProtocoloRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.data.repository.UsuarioRepository

// ': Application()' = hereda de la clase Application de Android. Vive mientras la app esté en memoria.
class InduztekApp : Application() {

    // 'by lazy' = se crea la PRIMERA vez que alguien la usa (no antes). 'this' es el contexto de la aplicación.
    val database by lazy { AppDatabase.getDatabase(this) }
    // Cada repositorio recibe el DAO que necesita. Así se conectan las capas (inyección de dependencias 'a mano').
    val equipoRepository by lazy { EquipoRepository(database.equipoDao()) }
    val pruebaRepository by lazy { PruebaRepository(database.pruebaDao()) }
    val protocoloRepository by lazy { ProtocoloRepository(database.protocoloDao()) }
    // Este repositorio necesita dos DAOs: usuarios y códigos de recuperación.
    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao(), database.codigoRecuperacionDao()) }
}
