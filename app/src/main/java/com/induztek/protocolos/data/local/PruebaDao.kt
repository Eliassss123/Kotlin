// ============================================================================
// ARCHIVO : data/local/PruebaDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : consultas sobre la tabla pruebas.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {
    // Pruebas de UN equipo (historial), ordenadas de la más nueva a la más antigua.
    @Query("SELECT * FROM pruebas WHERE equipoCodigo = :equipoCodigo ORDER BY fechaHora DESC")
    fun getPruebasByEquipo(equipoCodigo: String): Flow<List<PruebaEntity>>

    // Todas las pruebas.
    @Query("SELECT * FROM pruebas ORDER BY fechaHora DESC")
    fun getAllPruebas(): Flow<List<PruebaEntity>>

    // Guarda una prueba.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrueba(prueba: PruebaEntity)

    // Guarda varias pruebas de una vez (datos de ejemplo).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pruebas: List<PruebaEntity>)

    // Cuenta las filas (para sembrar datos de ejemplo si está vacía).
    @Query("SELECT COUNT(*) FROM pruebas")
    suspend fun getCount(): Int
}
