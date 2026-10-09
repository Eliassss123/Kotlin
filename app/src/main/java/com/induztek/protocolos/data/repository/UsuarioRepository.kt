// ============================================================================
// ARCHIVO : data/repository/UsuarioRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Gestión de usuarios, perfiles (P-01 a P-04), activación/desactivación (RF-22)
//           y códigos de recuperación de contraseña.
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.CodigoRecuperacionDao
import com.induztek.protocolos.data.local.UsuarioDao
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.CodigoRecuperacion
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val codigoRecuperacionDao: CodigoRecuperacionDao
) {
    val allUsuarios: Flow<List<UsuarioEntity>> = usuarioDao.getAllUsuarios()

    suspend fun buscarPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.buscarPorCorreo(correo.trim())
    }

    suspend fun buscarPorId(id: String): UsuarioEntity? {
        return usuarioDao.buscarPorId(id)
    }

    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Boolean {
        return usuarioDao.actualizarContrasena(correo.trim(), nuevaContrasena) > 0
    }

    // RF-22: Activar o desactivar personal
    suspend fun setActivo(id: String, activo: Boolean): Boolean {
        return usuarioDao.setActivo(id, activo) > 0
    }

    suspend fun insertarUsuario(usuario: UsuarioEntity) {
        usuarioDao.insertarUsuario(usuario)
    }

    suspend fun guardarCodigoRecuperacion(codigo: CodigoRecuperacion) {
        codigoRecuperacionDao.insertarCodigo(codigo)
    }

    suspend fun obtenerUltimoCodigo(correo: String): CodigoRecuperacion? {
        return codigoRecuperacionDao.obtenerUltimoCodigo(correo.trim())
    }

    suspend fun marcarCodigoComoUsado(id: String) {
        codigoRecuperacionDao.marcarComoUsado(id)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (usuarioDao.getCount() == 0) {
            val initialUsers = listOf(
                UsuarioEntity(
                    id = "USR-001",
                    correo = "tecnico@induztek.cl",
                    contrasena = "Tecnico2026!",
                    nombre = "Juan Pérez",
                    perfilCodigo = "P-01",
                    empresaCliente = null,
                    activo = true
                ),
                UsuarioEntity(
                    id = "USR-002",
                    correo = "ingeniero@induztek.cl",
                    contrasena = "Ingeniero2026!",
                    nombre = "Carlos Mendoza",
                    perfilCodigo = "P-02",
                    empresaCliente = null,
                    activo = true
                ),
                UsuarioEntity(
                    id = "USR-003",
                    correo = "jefetecnico@induztek.cl",
                    contrasena = "Jefe2026!",
                    nombre = "Roberto Silva",
                    perfilCodigo = "P-03",
                    empresaCliente = null,
                    activo = true
                ),
                UsuarioEntity(
                    id = "USR-004",
                    correo = "cliente@plantalampa.cl",
                    contrasena = "Cliente2026!",
                    nombre = "Mario Gómez (Jefe Mantención)",
                    perfilCodigo = "P-04",
                    empresaCliente = "Planta Lampa",
                    activo = true
                ),
                UsuarioEntity(
                    id = "USR-005",
                    correo = "admin@induztek.cl",
                    contrasena = "Admin2026!",
                    nombre = "Administrador Induztek",
                    perfilCodigo = "P-03",
                    empresaCliente = null,
                    activo = true
                )
            )
            usuarioDao.insertarUsuarios(initialUsers)
        }
    }
}
