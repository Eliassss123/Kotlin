// ============================================================================
// ARCHIVO : data/local/UsuarioDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : consultas sobre la tabla usuarios (login y cambio de contraseña).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {

    // Busca un usuario por correo, sin importar mayúsculas (LOWER). LIMIT 1 = solo uno. Devuelve null si no existe.
    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    // Cambia la contraseña. Devuelve un Int = cuántas filas se modificaron (0 = no existía ese correo).
    @Query("UPDATE usuarios SET contrasena = :nuevaContrasena WHERE LOWER(correo) = LOWER(:correo)")
    suspend fun actualizarContrasena(correo: String, nuevaContrasena: String): Int

    // Inserta un usuario.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    // Inserta varios usuarios (usuarios de ejemplo).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuarios(usuarios: List<UsuarioEntity>)

    // Cuenta usuarios (si es 0 se crean los de ejemplo).
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getCount(): Int
}
