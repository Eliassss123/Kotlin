// ============================================================================
// ARCHIVO : data/local/ActaDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla actas.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActaDao {
    @Query("SELECT * FROM actas ORDER BY fechaEmision DESC")
    fun getAllActas(): Flow<List<ActaEntity>>

    @Query("SELECT * FROM actas WHERE id = :id")
    suspend fun getActaById(id: String): ActaEntity?

    @Query("SELECT * FROM actas WHERE visitaId = :visitaId LIMIT 1")
    suspend fun getActaByVisita(visitaId: String): ActaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActa(acta: ActaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(actas: List<ActaEntity>)

    @Query("SELECT COUNT(*) FROM actas")
    suspend fun getCount(): Int
}
