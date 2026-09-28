// ============================================================================
// ARCHIVO : data/local/CodigoRecuperacionDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : consultas sobre la tabla codigos_recuperacion.
//           DAO = Data Access Object: una INTERFAZ donde declaras las consultas; Room escribe el código.
//           'suspend' = función que puede tardar y se llama desde una corrutina (segundo plano).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.induztek.protocolos.model.CodigoRecuperacion

// @Dao: marca esta interfaz como acceso a datos.
@Dao
interface CodigoRecuperacionDao {

    // Inserta un código. REPLACE = si ya existe uno con el mismo id, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCodigo(codigo: CodigoRecuperacion)

    // SQL: trae el código MÁS RECIENTE (ORDER BY timestamp DESC LIMIT 1) de ese correo que aún no se usó (usado = 0).
    // ':correo' es el parámetro de la función. LOWER(...) ignora mayúsculas. '?' en el tipo = puede devolver null.
    @Query("SELECT * FROM codigos_recuperacion WHERE LOWER(correoAsociado) = LOWER(:correo) AND usado = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun obtenerUltimoCodigo(correo: String): CodigoRecuperacion?

    // SQL: marca el código como usado (usado = 1) para que no se pueda reutilizar.
    @Query("UPDATE codigos_recuperacion SET usado = 1 WHERE id = :id")
    suspend fun marcarComoUsado(id: String)
}
