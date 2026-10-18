// ============================================================================
// ARCHIVO : data/local/ActaDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla actas.
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [ActaEntity].
 * Proporciona métodos para consultar, insertar y contar actas de entrega en la base de datos local.
 */
@Dao
interface ActaDao {
    /**
     * Obtiene un flujo ([Flow]) con la lista de todas las actas ordenadas por fecha de emisión descendente.
     * @return Flow con la lista de [ActaEntity].
     */
    @Query("SELECT * FROM actas ORDER BY fechaEmision DESC")
    fun getAllActas(): Flow<List<ActaEntity>>

    /**
     * Busca y retorna un acta específica por su identificador único.
     * @param id Identificador único del acta.
     * @return [ActaEntity] si existe, o null en caso contrario.
     */
    @Query("SELECT * FROM actas WHERE id = :id")
    suspend fun getActaById(id: String): ActaEntity?

    /**
     * Busca y retorna el acta asociada a una visita específica.
     * @param visitaId Identificador único de la visita.
     * @return [ActaEntity] asociada a la visita, o null.
     */
    @Query("SELECT * FROM actas WHERE visitaId = :visitaId LIMIT 1")
    suspend fun getActaByVisita(visitaId: String): ActaEntity?

    /**
     * Inserta o actualiza un acta en la base de datos local.
     * @param acta Entidad [ActaEntity] a insertar o actualizar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActa(acta: ActaEntity)

    /**
     * Inserta o actualiza una lista de actas en la base de datos local.
     * @param actas Lista de entidades [ActaEntity].
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(actas: List<ActaEntity>)

    /**
     * Retorna el número total de actas almacenadas en la tabla.
     * @return Conteo total de actas.
     */
    @Query("SELECT COUNT(*) FROM actas")
    suspend fun getCount(): Int
}
