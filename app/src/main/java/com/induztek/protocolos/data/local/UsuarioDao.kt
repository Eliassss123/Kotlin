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

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAllUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE correo = :correo")
    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Int

    @Query("UPDATE usuarios SET activo = :activo WHERE id = :id")
    suspend fun setActivo(id: String, activo: Boolean): Int

    @Query("UPDATE usuarios SET fotoPerfil = :fotoPath WHERE id = :id")
    suspend fun actualizarFotoPerfil(id: String, fotoPath: String?): Int

    @Query("UPDATE usuarios SET biografia = :biografia WHERE id = :id")
    suspend fun actualizarBiografia(id: String, biografia: String?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int
}
