package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    fun getAllEquipos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun getEquipoById(id: String): EquipoEntity?

    @Query("SELECT * FROM equipos WHERE codigo = :codigo")
    suspend fun getEquipoByCodigo(codigo: String): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(equipos: List<EquipoEntity>)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int
}
