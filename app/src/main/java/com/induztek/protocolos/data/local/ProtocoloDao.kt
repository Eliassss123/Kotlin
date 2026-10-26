






package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ProtocoloDao {
    
    @Query("SELECT * FROM protocolos ORDER BY fechaHora DESC")
    fun getAllProtocolos(): Flow<List<ProtocoloEntity>>

    
    @Query("SELECT * FROM protocolos WHERE visitaId = :visitaId ORDER BY fechaHora DESC")
    fun getProtocolosByVisita(visitaId: String): Flow<List<ProtocoloEntity>>

    
    @Query("SELECT * FROM protocolos WHERE equipoCodigo = :equipoCodigo ORDER BY fechaHora DESC")
    fun getProtocolosByEquipo(equipoCodigo: String): Flow<List<ProtocoloEntity>>

    
    @Query("SELECT * FROM protocolos WHERE id = :id LIMIT 1")
    suspend fun getProtocoloById(id: String): ProtocoloEntity?

    
    @Query("SELECT * FROM protocolos WHERE visitaId = :visitaId AND equipoCodigo = :equipoCodigo LIMIT 1")
    suspend fun getProtocoloByVisitaYEquipo(visitaId: String, equipoCodigo: String): ProtocoloEntity?

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocolo(protocolo: ProtocoloEntity)

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocolos: List<ProtocoloEntity>)

    
    @Update
    suspend fun updateProtocolo(protocolo: ProtocoloEntity)

    
    @Query("UPDATE protocolos SET estado = :nuevoEstado, formalizadoPor = :formalizadoPor, fechaFormalizacion = :fechaFormalizacion WHERE id = :id")
    suspend fun formalizarProtocolo(id: String, nuevoEstado: String, formalizadoPor: String, fechaFormalizacion: String)

    
    @Query("SELECT COUNT(*) FROM protocolos")
    suspend fun getCount(): Int
}
