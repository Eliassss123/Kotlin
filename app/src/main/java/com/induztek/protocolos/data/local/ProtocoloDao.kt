package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocoloDao {
    @Query("SELECT * FROM protocolos ORDER BY fechaHora DESC")
    fun getAllProtocolos(): Flow<List<ProtocoloEntity>>

    @Query("SELECT * FROM protocolos WHERE id = :id")
    suspend fun getProtocoloById(id: String): ProtocoloEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocolo(protocolo: ProtocoloEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocolos: List<ProtocoloEntity>)

    @Query("SELECT COUNT(*) FROM protocolos")
    suspend fun getCount(): Int
}
