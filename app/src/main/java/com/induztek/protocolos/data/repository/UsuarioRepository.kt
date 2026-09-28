// ============================================================================
// ARCHIVO : data/repository/UsuarioRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : acceso a usuarios y códigos de recuperación. Es un intermediario delgado: casi todo delega en los DAOs.
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.CodigoRecuperacionDao
import com.induztek.protocolos.data.local.UsuarioDao
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.CodigoRecuperacion

// Recibe DOS DAOs: uno de usuarios y otro de códigos.
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val codigoRecuperacionDao: CodigoRecuperacionDao
) {
    // trim() quita espacios al inicio/final del correo antes de buscar.
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.buscarPorCorreo(correo.trim())
    }

    // Devuelve true si se modificó al menos una fila (> 0), es decir, si el correo existía.
    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Boolean {
        return usuarioDao.actualizarContrasena(correo.trim(), nuevaContrasena) > 0
    }

    suspend fun insertarUsuario(usuario: UsuarioEntity) {
        usuarioDao.insertarUsuario(usuario)
    }

    suspend fun guardarCodigoRecuperacion(codigo: CodigoRecuperacion) {
        codigoRecuperacionDao.insertarCodigo(codigo)
    }

    // Último código NO usado de ese correo.
    suspend fun obtenerUltimoCodigo(correo: String): CodigoRecuperacion? {
        return codigoRecuperacionDao.obtenerUltimoCodigo(correo.trim())
    }

    // Invalida el código para que no se pueda reusar.
    suspend fun marcarCodigoComoUsado(id: String) {
        codigoRecuperacionDao.marcarComoUsado(id)
    }

    // Crea 2 usuarios de PRUEBA si no hay ninguno:
    //   admin@induztek.cl   / Admin2026!
    //   tecnico@induztek.cl / Tecnico2026!
    suspend fun seedInitialDataIfEmpty() {
        if (usuarioDao.getCount() == 0) {
            val initialUsers = listOf(
                UsuarioEntity(
                    id = "USR-001",
                    correo = "admin@induztek.cl",
                    contrasena = "Admin2026!",
                    nombre = "Administrador Induztek"
                ),
                UsuarioEntity(
                    id = "USR-002",
                    correo = "tecnico@induztek.cl",
                    contrasena = "Tecnico2026!",
                    nombre = "Tecnico Terreno"
                )
            )
            usuarioDao.insertarUsuarios(initialUsers)
        }
    }
}
