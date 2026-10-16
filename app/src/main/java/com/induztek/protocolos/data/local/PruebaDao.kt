// ============================================================================
// ARCHIVO : data/local/PruebaDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla pruebas, incluyendo obtención de la última prueba
//           previa para comparación histórica (RN-09, RT-06).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PruebaDao {
    @Query("SELECT * FROM pruebas WHERE equipoCodigo = :equipoCodigo ORDER BY fechaHora DESC")
    fun getPruebasByEquipo(equipoCodigo: String): Flow<List<PruebaEntity>>

    @Query("SELECT * FROM pruebas WHERE protocoloId = :protocoloId ORDER BY id ASC")
    fun getPruebasByProtocolo(protocoloId: String): Flow<List<PruebaEntity>>

    // RN-09, RT-06: Última prueba del mismo tipo del mismo equipo para calcular variación
    @Query("SELECT * FROM pruebas WHERE equipoCodigo = :equipoCodigo AND tipoPrueba = :tipoPrueba AND estadoPrueba = 'Realizada' ORDER BY fechaHora DESC LIMIT 1")
    suspend fun getUltimaPruebaPorTipo(equipoCodigo: String, tipoPrueba: String): PruebaEntity?

    @Query("SELECT * FROM pruebas ORDER BY fechaHora DESC")
    fun getAllPruebas(): Flow<List<PruebaEntity>>

    @Query("SELECT * FROM pruebas WHERE id = :id LIMIT 1")
    suspend fun getPruebaById(id: String): PruebaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrueba(prueba: PruebaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pruebas: List<PruebaEntity>)

    @Query("SELECT COUNT(*) FROM pruebas")
    suspend fun getCount(): Int
}
