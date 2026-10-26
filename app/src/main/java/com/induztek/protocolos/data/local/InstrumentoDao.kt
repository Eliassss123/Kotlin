





package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface InstrumentoDao {
    
    @Query("SELECT * FROM instrumentos ORDER BY tipo ASC, marca ASC")
    fun getAllInstrumentos(): Flow<List<InstrumentoEntity>>

    
    @Query("SELECT * FROM instrumentos WHERE activo = 1 ORDER BY tipo ASC")
    fun getInstrumentosActivos(): Flow<List<InstrumentoEntity>>

    
    @Query("SELECT * FROM instrumentos WHERE id = :id")
    suspend fun getInstrumentoById(id: String): InstrumentoEntity?

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstrumento(instrumento: InstrumentoEntity)

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instrumentos: List<InstrumentoEntity>)

    
    @Update
    suspend fun updateInstrumento(instrumento: InstrumentoEntity)

    
    @Query("UPDATE instrumentos SET vencimientoCalibracion = :nuevoVencimiento WHERE id = :id")
    suspend fun actualizarCalibracion(id: String, nuevoVencimiento: String)

    
    @Query("SELECT COUNT(*) FROM instrumentos")
    suspend fun getCount(): Int
}
