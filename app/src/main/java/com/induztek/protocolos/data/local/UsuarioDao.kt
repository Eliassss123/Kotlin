// ============================================================================
// ARCHIVO : data/local/UsuarioDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla usuarios.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [UsuarioEntity].
 * Administra autenticación, perfiles P-01 a P-04, activación/desactivación de personal (RF-22) y recuperación de claves.
 */
@Dao
interface UsuarioDao {
    /**
     * Obtiene un flujo con todos los usuarios ordenados por nombre alfabéticamente.
     */
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>

    /**
     * Busca un usuario por su correo electrónico.
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    /**
     * Busca un usuario por su ID único.
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    /**
     * Actualiza la contraseña de un usuario por su correo.
     */
    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE correo = :correo")
    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Int

    /**
     * RF-22: Activa o desactiva un usuario del personal.
     */
    @Query("UPDATE usuarios SET activo = :activo WHERE id = :id")
    suspend fun setActivo(id: String, activo: Boolean): Int

    /**
     * Actualiza la ruta de la foto de perfil de un usuario.
     */
    @Query("UPDATE usuarios SET fotoPerfil = :fotoPath WHERE id = :id")
    suspend fun actualizarFotoPerfil(id: String, fotoPath: String?): Int

    /**
     * Actualiza la biografía del usuario.
     */
    @Query("UPDATE usuarios SET biografia = :biografia WHERE id = :id")
    suspend fun actualizarBiografia(id: String, biografia: String?): Int

    /**
     * Inserta un usuario.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    /**
     * Inserta una lista de usuarios.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    /**
     * Actualiza un usuario existente.
     */
    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    /**
     * Retorna el número total de usuarios registrados.
     */
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int
}
