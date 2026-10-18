// ============================================================================
// ARCHIVO : data/local/ProtocoloDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla protocolos. Incluye control de unicidad (visita, equipo)
//           según RN-02 y Aclaración 5, formalización y consultas por equipo.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [ProtocoloEntity].
 * Administra consultas, unicidad por visita y equipo (RN-02), formalización (RN-04) y almacenamiento de protocolos.
 */
@Dao
interface ProtocoloDao {
    /**
     * Obtiene un flujo con todos los protocolos ordenados por fecha y hora descendente.
     */
    @Query("SELECT * FROM protocolos ORDER BY fechaHora DESC")
    fun getAllProtocolos(): Flow<List<ProtocoloEntity>>

    /**
     * Obtiene un flujo con los protocolos asociados a una visita específica.
     */
    @Query("SELECT * FROM protocolos WHERE visitaId = :visitaId ORDER BY fechaHora DESC")
    fun getProtocolosByVisita(visitaId: String): Flow<List<ProtocoloEntity>>

    /**
     * Obtiene un flujo con los protocolos históricos de un equipo específico.
     */
    @Query("SELECT * FROM protocolos WHERE equipoCodigo = :equipoCodigo ORDER BY fechaHora DESC")
    fun getProtocolosByEquipo(equipoCodigo: String): Flow<List<ProtocoloEntity>>

    /**
     * Busca un protocolo por su ID único.
     */
    @Query("SELECT * FROM protocolos WHERE id = :id LIMIT 1")
    suspend fun getProtocoloById(id: String): ProtocoloEntity?

    /**
     * RN-02, Aclaración 5: Bloquear un segundo protocolo del mismo equipo en la misma visita.
     */
    @Query("SELECT * FROM protocolos WHERE visitaId = :visitaId AND equipoCodigo = :equipoCodigo LIMIT 1")
    suspend fun getProtocoloByVisitaYEquipo(visitaId: String, equipoCodigo: String): ProtocoloEntity?

    /**
     * Inserta o actualiza un protocolo.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocolo(protocolo: ProtocoloEntity)

    /**
     * Inserta o actualiza una lista de protocolos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocolos: List<ProtocoloEntity>)

    /**
     * Actualiza un protocolo existente.
     */
    @Update
    suspend fun updateProtocolo(protocolo: ProtocoloEntity)

    /**
     * RN-04, RN-16, RT-09: Actualiza el estado de formalización de un protocolo.
     */
    @Query("UPDATE protocolos SET estado = :nuevoEstado, formalizadoPor = :formalizadoPor, fechaFormalizacion = :fechaFormalizacion WHERE id = :id")
    suspend fun formalizarProtocolo(id: String, nuevoEstado: String, formalizadoPor: String, fechaFormalizacion: String)

    /**
     * Retorna el número total de protocolos almacenados.
     */
    @Query("SELECT COUNT(*) FROM protocolos")
    suspend fun getCount(): Int
}
