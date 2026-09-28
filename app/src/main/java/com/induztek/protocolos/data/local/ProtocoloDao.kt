// ============================================================================
// ARCHIVO : data/local/ProtocoloDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : consultas sobre la tabla protocolos.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocoloDao {
    // Todos los protocolos, del más reciente al más antiguo (DESC = descendente). Flow = se actualiza solo.
    @Query("SELECT * FROM protocolos ORDER BY fechaHora DESC")
    fun getAllProtocolos(): Flow<List<ProtocoloEntity>>

    // Un protocolo por su id.
    @Query("SELECT * FROM protocolos WHERE id = :id")
    suspend fun getProtocoloById(id: String): ProtocoloEntity?

    // Guarda un protocolo (reemplaza si ya existe el id).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocolo(protocolo: ProtocoloEntity)

    // Guarda varios de una vez (se usa para los datos de ejemplo).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocolos: List<ProtocoloEntity>)

    // Cantidad de protocolos; sirve para saber si hay que sembrar datos de ejemplo.
    @Query("SELECT COUNT(*) FROM protocolos")
    suspend fun getCount(): Int
}
