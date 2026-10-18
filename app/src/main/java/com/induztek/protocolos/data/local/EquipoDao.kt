// ============================================================================
// ARCHIVO : data/local/EquipoDao.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Consultas sobre la tabla equipos con soporte para filtros por cliente (P-04)
//           y detección de duplicados en la misma instalación (RT-02).
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [EquipoEntity].
 * Proporciona consultas para listar, buscar, filtrar por cliente y detectar duplicados de equipos.
 */
@Dao
interface EquipoDao {
    /**
     * Obtiene un flujo ([Flow]) con todos los equipos ordenados por código ascendentemente.
     */
    @Query("SELECT * FROM equipos ORDER BY codigo ASC")
    fun getAllEquipos(): Flow<List<EquipoEntity>>

    /**
     * Obtiene un flujo con los equipos pertenecientes a un cliente específico.
     * @param cliente Nombre o razón social del cliente.
     */
    @Query("SELECT * FROM equipos WHERE cliente = :cliente ORDER BY codigo ASC")
    fun getEquiposByCliente(cliente: String): Flow<List<EquipoEntity>>

    /**
     * Busca un equipo por su código único.
     * @param codigo Código del equipo (ej: TR-500KVA-01).
     */
    @Query("SELECT * FROM equipos WHERE codigo = :codigo LIMIT 1")
    suspend fun getEquipoByCodigo(codigo: String): EquipoEntity?

    /**
     * Busca un equipo por su identificador interno (ID).
     * @param id Identificador único del equipo.
     */
    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getEquipoById(id: String): EquipoEntity?

    /**
     * RT-02: Detección de duplicados (mismo TAG, número de serie o código en la misma instalación).
     * @param ubicacion Instalación o ubicación del equipo.
     * @param tag Tag del cliente.
     * @param numeroSerie Número de serie del equipo.
     * @param codigo Código del equipo.
     */
    @Query("SELECT * FROM equipos WHERE ubicacion = :ubicacion AND (tagCliente = :tag OR numeroSerie = :numeroSerie OR codigo = :codigo) LIMIT 1")
    suspend fun buscarPosibleDuplicado(ubicacion: String, tag: String, numeroSerie: String, codigo: String): EquipoEntity?

    /**
     * Inserta o actualiza un equipo en la base de datos local.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: EquipoEntity)

    /**
     * Inserta o actualiza una lista de equipos en la base de datos local.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(equipos: List<EquipoEntity>)

    /**
     * Retorna el número total de equipos registrados en la tabla.
     */
    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int
}
