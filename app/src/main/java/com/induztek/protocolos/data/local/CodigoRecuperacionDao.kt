







package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.induztek.protocolos.model.CodigoRecuperacion


@Dao
interface CodigoRecuperacionDao {

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCodigo(codigo: CodigoRecuperacion)

    
    
    @Query("SELECT * FROM codigos_recuperacion WHERE LOWER(correoAsociado) = LOWER(:correo) AND usado = 0 ORDER BY timestamp DESC LIMIT 1")
    suspend fun obtenerUltimoCodigo(correo: String): CodigoRecuperacion?

    
    @Query("UPDATE codigos_recuperacion SET usado = 1 WHERE id = :id")
    suspend fun marcarComoUsado(id: String)
}
