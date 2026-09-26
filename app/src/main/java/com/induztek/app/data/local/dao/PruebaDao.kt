package com.induztek.app.data.local.dao

import androidx.room.*
import com.induztek.app.data.local.entity.PruebaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la tabla pruebas.
 *
 * getPendienteSync() es el método más crítico del sistema offline-first:
 * retorna todas las pruebas cuyo estado == "PENDIENTE_SYNC" para que el
 * SyncWorker las procese cuando detecte conectividad.
 */
@Dao
interface PruebaDao {

    @Query("SELECT * FROM pruebas ORDER BY fecha DESC, hora DESC")
    fun getAll(): Flow<List<PruebaEntity>>

    @Query("SELECT * FROM pruebas WHERE equipoId = :equipoId ORDER BY fecha DESC, hora DESC")
    fun getByEquipo(equipoId: Long): Flow<List<PruebaEntity>>

    /** Pruebas pendientes de sincronización con el backend. */
    @Query("SELECT * FROM pruebas WHERE estado = 'PENDIENTE_SYNC'")
    fun getPendienteSync(): Flow<List<PruebaEntity>>

    @Query("SELECT * FROM pruebas WHERE id = :id")
    suspend fun getById(id: Long): PruebaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prueba: PruebaEntity): Long

    @Update
    suspend fun update(prueba: PruebaEntity)

    /**
     * Actualización parcial de estado — evita leer+escribir la fila completa.
     * Útil cuando el SyncWorker sólo necesita cambiar el campo estado y
     * sincronizadoAt sin modificar mediciones ni foto.
     */
    @Query("UPDATE pruebas SET estado = :estado, sincronizadoAt = :sincronizadoAt WHERE id = :id")
    suspend fun updateEstado(id: Long, estado: String, sincronizadoAt: String? = null)

    @Delete
    suspend fun delete(prueba: PruebaEntity)
}
