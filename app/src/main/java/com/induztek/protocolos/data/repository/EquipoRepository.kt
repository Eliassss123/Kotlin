package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.EquipoDao
import com.induztek.protocolos.data.local.EquipoEntity
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EquipoRepository(
    private val equipoDao: EquipoDao
) {
    val allEquipos: Flow<List<Equipo>> = equipoDao.getAllEquipos().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getEquipoByCodigo(codigo: String): Equipo? {
        return equipoDao.getEquipoByCodigo(codigo)?.toDomain()
    }

    suspend fun getEquipoById(id: String): Equipo? {
        return equipoDao.getEquipoById(id)?.toDomain()
    }

    suspend fun insertEquipo(equipo: Equipo) {
        equipoDao.insertEquipo(EquipoEntity.fromDomain(equipo))
    }

    suspend fun seedInitialDataIfEmpty() {
        if (equipoDao.getCount() == 0) {
            val initialEquipos = listOf(
                Equipo(
                    id = "EQ-001",
                    codigo = "TR-500KVA-01",
                    tipo = "Transformador de Potencia",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    ultimoEstado = EstadoProtocolo.FORMALIZADO
                ),
                Equipo(
                    id = "EQ-002",
                    codigo = "REL-SEL751-02",
                    tipo = "Relé de Protección",
                    ubicacion = "Planta Lampa - Alimentador 12kV",
                    ultimoEstado = EstadoProtocolo.REGISTRADO_TERRENO
                ),
                Equipo(
                    id = "EQ-003",
                    codigo = "CEL-MT24-03",
                    tipo = "Celda de Media Tensión",
                    ubicacion = "Subestación Distribución Sur",
                    ultimoEstado = EstadoProtocolo.FUERA_DE_RANGO
                )
            )
            equipoDao.insertAll(initialEquipos.map { EquipoEntity.fromDomain(it) })
        }
    }
}
