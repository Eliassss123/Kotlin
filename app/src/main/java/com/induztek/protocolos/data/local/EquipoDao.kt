






package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EquipoDao {
    
    @Query("SELECT * FROM equipos ORDER BY codigo ASC")
    fun getAllEquipos(): Flow<List<EquipoEntity>>

    
    @Query("SELECT * FROM equipos WHERE cliente = :cliente ORDER BY codigo ASC")
    fun getEquiposByCliente(cliente: String): Flow<List<EquipoEntity>>

    
    @Query("SELECT * FROM equipos WHERE codigo = :codigo LIMIT 1")
    suspend fun getEquipoByCodigo(codigo: String): EquipoEntity?

    
    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getEquipoById(id: String): EquipoEntity?

    
    @Query("SELECT * FROM equipos WHERE ubicacion = :ubicacion AND (tagCliente = :tag OR numeroSerie = :numeroSerie OR codigo = :codigo) LIMIT 1")
    suspend fun buscarPosibleDuplicado(ubicacion: String, tag: String, numeroSerie: String, codigo: String): EquipoEntity?

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(equipos: List<EquipoEntity>)

    
    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int
}
