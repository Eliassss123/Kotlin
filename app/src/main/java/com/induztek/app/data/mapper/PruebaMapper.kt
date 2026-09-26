package com.induztek.app.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.app.data.local.entity.PruebaEntity
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba

private val gson = Gson()
private val mapType = object : TypeToken<Map<String, String>>() {}.type

fun PruebaEntity.toDomain(): Prueba = Prueba(
    id             = id,
    equipoId       = equipoId,
    fecha          = fecha,
    hora           = hora,
    tecnico        = tecnico,
    mediciones     = gson.fromJson(medicionesJson, mapType) ?: emptyMap(),
    observaciones  = observaciones,
    fotoUriPath    = fotoUriPath,
    estado         = EstadoPrueba.valueOf(estado),
    sincronizadoAt = sincronizadoAt
)

fun Prueba.toEntity(): PruebaEntity = PruebaEntity(
    id             = id,
    equipoId       = equipoId,
    fecha          = fecha,
    hora           = hora,
    tecnico        = tecnico,
    medicionesJson = gson.toJson(mediciones),
    observaciones  = observaciones,
    fotoUriPath    = fotoUriPath,
    estado         = estado.name,
    sincronizadoAt = sincronizadoAt
)
