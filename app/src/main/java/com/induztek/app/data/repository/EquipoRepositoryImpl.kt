package com.induztek.app.data.repository

import com.induztek.app.data.local.dao.EquipoDao
import com.induztek.app.data.mapper.toDomain
import com.induztek.app.data.mapper.toEntity
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.repository.EquipoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipoRepositoryImpl @Inject constructor(
    private val dao: EquipoDao
) : EquipoRepository {

    override fun getAll(): Flow<List<Equipo>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: Long): Equipo? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(equipo: Equipo): Long =
        dao.insert(equipo.toEntity())

    override suspend fun update(equipo: Equipo) =
        dao.update(equipo.toEntity())

    override suspend fun delete(equipo: Equipo) =
        dao.delete(equipo.toEntity())
}
