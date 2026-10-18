// ============================================================================
// ARCHIVO : data/local/InstrumentoDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla instrumentos (Jefe Técnico P-03, RN-11, RT-11).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [InstrumentoEntity].
 * Administra el catastro de instrumentos de prueba, control de calibración y vigencia (RN-11, RT-11).
 */
@Dao
interface InstrumentoDao {
    /**
     * Obtiene un flujo con todos los instrumentos ordenados por tipo y marca.
     */
    @Query("SELECT * FROM instrumentos ORDER BY tipo ASC, marca ASC")
    fun getAllInstrumentos(): Flow<List<InstrumentoEntity>>

    /**
     * Obtiene un flujo con los instrumentos activos y habilitados para pruebas.
     */
    @Query("SELECT * FROM instrumentos WHERE activo = 1 ORDER BY tipo ASC")
    fun getInstrumentosActivos(): Flow<List<InstrumentoEntity>>

    /**
     * Busca un instrumento por su ID único.
     */
    @Query("SELECT * FROM instrumentos WHERE id = :id")
    suspend fun getInstrumentoById(id: String): InstrumentoEntity?

    /**
     * Inserta o actualiza un instrumento.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrumento(instrumento: InstrumentoEntity)

    /**
     * Inserta o actualiza una lista de instrumentos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instrumentos: List<InstrumentoEntity>)

    /**
     * Actualiza los datos de un instrumento existente.
     */
    @Update
    suspend fun updateInstrumento(instrumento: InstrumentoEntity)

    /**
     * Actualiza la fecha de vencimiento de calibración de un instrumento (RT-11).
     */
    @Query("UPDATE instrumentos SET vencimientoCalibracion = :nuevoVencimiento WHERE id = :id")
    suspend fun actualizarCalibracion(id: String, nuevoVencimiento: String)

    /**
     * Retorna el número total de instrumentos en la base de datos.
     */
    @Query("SELECT COUNT(*) FROM instrumentos")
    suspend fun getCount(): Int
}
