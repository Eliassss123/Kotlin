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

/**
 * Repositorio encargado de gestionar la autenticación, perfiles de usuario (P-01 a P-04), estado activo (RF-22)
 * y códigos de recuperación de contraseña.
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val codigoRecuperacionDao: CodigoRecuperacionDao
) {
    /** Flujo con todos los usuarios registrados. */
    val allUsuarios: Flow<List<UsuarioEntity>> = usuarioDao.getAllUsuarios()

    /**
     * Busca un usuario por correo electrónico.
     */
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity? {
        return usuarioDao.buscarPorCorreo(correo.trim())
    }

    /**
     * Busca un usuario por su ID único.
     */
    suspend fun buscarPorId(id: String): UsuarioEntity? {
        return usuarioDao.buscarPorId(id)
    }

    /**
     * Actualiza la contraseña de un usuario.
     */
    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Boolean {
        return usuarioDao.actualizarContrasena(correo.trim(), nuevaContrasena) > 0
    }

    /**
     * RF-22: Activa o desactiva un usuario del personal.
     */
    suspend fun setActivo(id: String, activo: Boolean): Boolean {
        return usuarioDao.setActivo(id, activo) > 0
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    suspend fun insertarUsuario(usuario: UsuarioEntity) {
        usuarioDao.insertarUsuario(usuario)
    }

    /**
     * Guarda un código de recuperación de contraseña.
     */
    suspend fun guardarCodigoRecuperacion(codigo: CodigoRecuperacion) {
        codigoRecuperacionDao.insertarCodigo(codigo)
    }

    /**
     * Obtiene el último código de recuperación asociado a un correo.
     */
    suspend fun obtenerUltimoCodigo(correo: String): CodigoRecuperacion? {
        return codigoRecuperacionDao.obtenerUltimoCodigo(correo.trim())
    }

    /**
     * Marca un código de recuperación como utilizado.
     */
    suspend fun marcarCodigoComoUsado(id: String) {
        codigoRecuperacionDao.marcarComoUsado(id)
    }

    /**
     * Actualiza la foto de perfil del usuario.
     */
    suspend fun actualizarFotoPerfil(id: String, fotoPath: String?): Boolean {
        return usuarioDao.actualizarFotoPerfil(id, fotoPath) > 0
    }

    /**
     * Actualiza la biografía del usuario.
     */
    suspend fun actualizarBiografia(id: String, biografia: String?): Boolean {
        return usuarioDao.actualizarBiografia(id, biografia) > 0
    }

    /**
     * Puebla la base de datos con los perfiles iniciales de demostración (P-01 a P-04) si está vacía.
     */
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
                    activo = true,
                    biografia = "Técnico de terreno especialista en pruebas de aislamiento y rigidez dieléctrica en transformadores y celdas de media tensión."
                ),
                UsuarioEntity(
                    id = "USR-002",
                    correo = "ingeniero@induztek.cl",
                    contrasena = "Ingeniero2026!",
                    nombre = "Carlos Mendoza",
                    perfilCodigo = "P-02",
                    empresaCliente = null,
                    activo = true,
                    biografia = "Ingeniero de pruebas eléctricas. Encargado de interpretar diagnósticos, formalizar protocolos de prueba y emitir observaciones técnicas."
                ),
                UsuarioEntity(
                    id = "USR-003",
                    correo = "jefetecnico@induztek.cl",
                    contrasena = "Jefe2026!",
                    nombre = "Roberto Silva",
                    perfilCodigo = "P-03",
                    empresaCliente = null,
                    activo = true,
                    biografia = "Jefe Técnico en Induztek. Responsable de la asignación de personal, control metrológico de instrumentos y formalización final de actas."
                ),
                UsuarioEntity(
                    id = "USR-004",
                    correo = "cliente@plantalampa.cl",
                    contrasena = "Cliente2026!",
                    nombre = "Mario Gómez (Jefe Mantención)",
                    perfilCodigo = "P-04",
                    empresaCliente = "Planta Lampa",
                    activo = true,
                    biografia = "Jefe de Mantención Eléctrica en Planta Lampa. Contraparte técnica para recepción, auditoría y consulta de protocolos de prueba."
                ),
                UsuarioEntity(
                    id = "USR-005",
                    correo = "admin@induztek.cl",
                    contrasena = "Admin2026!",
                    nombre = "Administrador Induztek",
                    perfilCodigo = "P-03",
                    empresaCliente = null,
                    activo = true,
                    biografia = "Administrador general del sistema Induztek."
                )
            )
            usuarioDao.insertarUsuarios(initialUsers)
        }
    }
}
