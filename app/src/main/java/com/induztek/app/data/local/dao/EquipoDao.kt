package com.induztek.app.data.local.dao

import androidx.room.*
import com.induztek.app.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la tabla equipos.
 *
 * Usamos Flow<List<>> en los métodos de consulta para que Room emita
 * automáticamente nuevos valores cuando la tabla cambie → la UI en Compose
 * recompone sola sin polling manual.
 *
 * Los métodos de escritura son suspend → se ejecutan en coroutines,
 * nunca en el hilo principal (Room lo exige con strictMode habilitado).
 */
@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY codigo ASC")
    fun getAll(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getById(id: Long): EquipoEntity?

    @Query("SELECT * FROM equipos WHERE codigo LIKE '%' || :query || '%' OR instalacion LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<EquipoEntity>>

    /** Retorna el rowId del nuevo registro (útil para navegar al detalle). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipo: EquipoEntity): Long

    @Update
    suspend fun update(equipo: EquipoEntity)

    @Delete
    suspend fun delete(equipo: EquipoEntity)
}
