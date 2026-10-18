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

/**
 * Data Access Object (DAO) para la entidad [VisitaEntity].
 * Administra el registro y consulta de visitas técnicas, incluyendo la visita activa en curso.
 */
@Dao
interface VisitaDao {
    /**
     * Obtiene un flujo con todas las visitas ordenadas por fecha y hora de inicio descendente.
     */
    @Query("SELECT * FROM visitas ORDER BY fecha DESC, horaInicio DESC")
    fun getAllVisitas(): Flow<List<VisitaEntity>>

    /**
     * Busca una visita por su ID único.
     */
    @Query("SELECT * FROM visitas WHERE id = :id")
    suspend fun getVisitaById(id: String): VisitaEntity?

    /**
     * Obtiene un flujo con la visita actualmente activa (no cerrada).
     */
    @Query("SELECT * FROM visitas WHERE cerrada = 0 ORDER BY fecha DESC, horaInicio DESC LIMIT 1")
    fun getVisitaActiva(): Flow<VisitaEntity?>

    /**
     * Inserta o actualiza una visita.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisita(visita: VisitaEntity)

    /**
     * Inserta o actualiza una lista de visitas.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(visitas: List<VisitaEntity>)

    /**
     * Actualiza una visita existente.
     */
    @Update
    suspend fun updateVisita(visita: VisitaEntity)

    /**
     * Retorna el número total de visitas registradas.
     */
    @Query("SELECT COUNT(*) FROM visitas")
    suspend fun getCount(): Int
}
