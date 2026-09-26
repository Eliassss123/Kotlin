package com.induztek.app.domain.repository

import com.induztek.app.domain.model.Equipo
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de Equipos — capa domain.
 *
 * Decisión de diseño: el dominio define INTERFACES, nunca implementaciones.
 * Esto permite:
 *   1. Inyectar un FakeEquipoRepository en tests unitarios.
 *   2. Cambiar la fuente de datos (Room → red) sin tocar ViewModels ni UseCases.
 *
 * Retornamos Flow<List<Equipo>> para que la UI reaccione automáticamente
 * cuando los datos cambien en Room (Room emite nuevos valores por cada INSERT/UPDATE).
 */
interface EquipoRepository {
    fun getAll(): Flow<List<Equipo>>
    suspend fun getById(id: Long): Equipo?
    suspend fun insert(equipo: Equipo): Long
    suspend fun update(equipo: Equipo)
    suspend fun delete(equipo: Equipo)
}
