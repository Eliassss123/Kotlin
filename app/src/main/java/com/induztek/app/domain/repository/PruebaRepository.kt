package com.induztek.app.domain.repository

import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de Pruebas — capa domain.
 *
 * getPendienteSync() es clave para la estrategia offline-first:
 * el SyncWorker consulta este método para obtener pruebas que aún
 * no se han sincronizado con el backend y las envía vía Retrofit.
 */
interface PruebaRepository {
    fun getAll(): Flow<List<Prueba>>
    fun getByEquipo(equipoId: Long): Flow<List<Prueba>>
    fun getPendienteSync(): Flow<List<Prueba>>   // estado == PENDIENTE_SYNC
    suspend fun getById(id: Long): Prueba?
    suspend fun insert(prueba: Prueba): Long
    suspend fun update(prueba: Prueba)
    suspend fun updateEstado(id: Long, nuevoEstado: EstadoPrueba)
    suspend fun delete(prueba: Prueba)
}
