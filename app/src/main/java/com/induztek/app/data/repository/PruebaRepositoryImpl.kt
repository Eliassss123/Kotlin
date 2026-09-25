package com.induztek.app.data.repository

import com.induztek.app.data.local.dao.PruebaDao
import com.induztek.app.data.mapper.toDomain
import com.induztek.app.data.mapper.toEntity
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import com.induztek.app.domain.repository.PruebaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación real del contrato PruebaRepository.
 *
 * Esta clase vive en la capa data y es la ÚNICA que conoce Room.
 * Los ViewModels y UseCases dependen de PruebaRepository (interfaz),
 * no de esta clase concreta → Hilt inyecta esta implementación en runtime.
 *
 * Estrategia offline-first:
 * - insert() guarda con estado REGISTRADO_TERRENO (o PENDIENTE_SYNC si
 *   el técnico eligió sincronizar inmediatamente).
 * - updateEstado() es llamado por SyncWorker cuando la sync tiene éxito.
 *
 * @Singleton → una sola instancia compartida en toda la app.
 */
@Singleton
class PruebaRepositoryImpl @Inject constructor(
    private val dao: PruebaDao
) : PruebaRepository {

    override fun getAll(): Flow<List<Prueba>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getByEquipo(equipoId: Long): Flow<List<Prueba>> =
        dao.getByEquipo(equipoId).map { entities -> entities.map { it.toDomain() } }

    override fun getPendienteSync(): Flow<List<Prueba>> =
        dao.getPendienteSync().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: Long): Prueba? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(prueba: Prueba): Long =
        dao.insert(prueba.toEntity())

    override suspend fun update(prueba: Prueba) =
        dao.update(prueba.toEntity())

    override suspend fun updateEstado(id: Long, nuevoEstado: EstadoPrueba) {
        val sincronizadoAt = if (nuevoEstado == EstadoPrueba.SINCRONIZADO) {
            java.time.Instant.now().toString()   // ISO-8601
        } else null
        dao.updateEstado(id, nuevoEstado.name, sincronizadoAt)
    }

    override suspend fun delete(prueba: Prueba) =
        dao.delete(prueba.toEntity())
}
