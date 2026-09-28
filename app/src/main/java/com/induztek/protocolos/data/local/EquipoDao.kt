// ============================================================================
// ARCHIVO : data/local/EquipoDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : consultas sobre la tabla equipos (ver CodigoRecuperacionDao.kt para la explicación de qué es un DAO).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    // Trae TODOS los equipos. Devuelve Flow: un flujo 'vivo' que vuelve a emitir la lista cuando la tabla cambia (no
    //   es 'suspend' porque el Flow ya es asíncrono).
    @Query("SELECT * FROM equipos")
    fun getAllEquipos(): Flow<List<EquipoEntity>>

    // Busca un equipo por id. El tipo 'EquipoEntity?' indica que puede no existir (null).
    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getEquipoById(id: String): EquipoEntity?

    // Busca un equipo por su código.
    @Query("SELECT * FROM equipos WHERE codigo = :codigo")
    suspend fun getEquipoByCodigo(codigo: String): EquipoEntity?

    // Inserta (o reemplaza si ya existe el mismo id). Por eso también sirve para 'actualizar'.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    // Igual, pero inserta una lista completa de una vez.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(equipos: List<EquipoEntity>)

    // Cuenta cuántas filas hay. Se usa para saber si la tabla está vacía y sembrar datos de ejemplo.
    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int
}
