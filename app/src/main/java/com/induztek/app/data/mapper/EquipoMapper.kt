package com.induztek.app.data.mapper

import com.induztek.app.data.local.entity.EquipoEntity
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.TipoEquipo

/**
 * Mapper entre EquipoEntity (capa data) y Equipo (capa domain).
 *
 * Decisión de diseño: las funciones de mapeo son extensiones puras (sin estado,
 * sin dependencias inyectadas). Esto las hace trivialmente testables con JUnit.
 *
 * Flujo:
 *   Room → EquipoEntity.toDomain() → Equipo (ViewModel/UseCase)
 *   Equipo.toEntity()              → EquipoEntity → Room
 */

fun EquipoEntity.toDomain(): Equipo = Equipo(
    id          = id,
    codigo      = codigo,
    tipo        = TipoEquipo.valueOf(tipo),   // String → enum
    instalacion = instalacion,
    descripcion = descripcion
)

fun Equipo.toEntity(): EquipoEntity = EquipoEntity(
    id          = id,
    codigo      = codigo,
    tipo        = tipo.name,                  // enum → String
    instalacion = instalacion,
    descripcion = descripcion
)
