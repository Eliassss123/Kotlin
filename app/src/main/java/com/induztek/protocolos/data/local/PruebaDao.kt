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

    @Query("SELECT * FROM pruebas ORDER BY fechaHora DESC")
    fun getAllPruebas(): Flow<List<PruebaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrueba(prueba: PruebaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pruebas: List<PruebaEntity>)

    @Query("SELECT COUNT(*) FROM pruebas")
    suspend fun getCount(): Int
}
