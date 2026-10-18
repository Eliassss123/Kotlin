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

/**
 * Data Access Object (DAO) para la entidad [PruebaEntity].
 * Administra el almacenamiento de pruebas técnicas e histórico de resultados para comparación (RN-09).
 */
@Dao
interface PruebaDao {
    /**
     * Obtiene un flujo con las pruebas de un equipo ordenadas por fecha descendente.
     */
    @Query("SELECT * FROM pruebas WHERE equipoCodigo = :equipoCodigo ORDER BY fechaHora DESC")
    fun getPruebasByEquipo(equipoCodigo: String): Flow<List<PruebaEntity>>

    /**
     * Obtiene un flujo con las pruebas asociadas a un protocolo.
     */
    @Query("SELECT * FROM pruebas WHERE protocoloId = :protocoloId ORDER BY id ASC")
    fun getPruebasByProtocolo(protocoloId: String): Flow<List<PruebaEntity>>

    /**
     * RN-09, RT-06: Obtiene la última prueba realizada del mismo tipo para calcular variación histórica.
     */
    @Query("SELECT * FROM pruebas WHERE equipoCodigo = :equipoCodigo AND tipoPrueba = :tipoPrueba AND estadoPrueba = 'Realizada' ORDER BY fechaHora DESC LIMIT 1")
    suspend fun getUltimaPruebaPorTipo(equipoCodigo: String, tipoPrueba: String): PruebaEntity?

    /**
     * Obtiene un flujo con todas las pruebas registradas.
     */
    @Query("SELECT * FROM pruebas ORDER BY fechaHora DESC")
    fun getAllPruebas(): Flow<List<PruebaEntity>>

    /**
     * Busca una prueba por su ID único.
     */
    @Query("SELECT * FROM pruebas WHERE id = :id LIMIT 1")
    suspend fun getPruebaById(id: String): PruebaEntity?

    /**
     * Inserta o actualiza una prueba técnica.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrueba(prueba: PruebaEntity)

    /**
     * Inserta o actualiza una lista de pruebas técnicas.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pruebas: List<PruebaEntity>)

    /**
     * Retorna el número total de pruebas registradas.
     */
    @Query("SELECT COUNT(*) FROM pruebas")
    suspend fun getCount(): Int
}
