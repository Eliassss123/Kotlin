// ============================================================================
// ARCHIVO : data/local/VisitaDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla visitas.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitaDao {
    @Query("SELECT * FROM visitas ORDER BY fecha DESC, horaInicio DESC")
    fun getAllVisitas(): Flow<List<VisitaEntity>>

    @Query("SELECT * FROM visitas WHERE id = :id")
    suspend fun getVisitaById(id: String): VisitaEntity?

    @Query("SELECT * FROM visitas WHERE cerrada = 0 ORDER BY fecha DESC, horaInicio DESC LIMIT 1")
    fun getVisitaActiva(): Flow<VisitaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisita(visita: VisitaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visitas: List<VisitaEntity>)

    @Update
    suspend fun updateVisita(visita: VisitaEntity)

    @Query("SELECT COUNT(*) FROM visitas")
    suspend fun getCount(): Int
}
