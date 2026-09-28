// ============================================================================
// ARCHIVO : data/repository/EquipoRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : intermediario entre el ViewModel y la base de datos. El ViewModel no sabe de dónde vienen los datos
//           (Room, internet...). Además convierte Entity (formato de la BD) <-> modelo de dominio (formato de la
//             app).
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.EquipoDao
import com.induztek.protocolos.data.local.EquipoEntity
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Recibe el DAO por el constructor (así es fácil reemplazarlo en pruebas).
class EquipoRepository(
    private val equipoDao: EquipoDao
) {
    // Flow<List<Equipo>> = flujo 'vivo': cuando la tabla cambia, emite la lista nueva y la pantalla se redibuja sola.
    // '.map { }' transforma cada EquipoEntity en Equipo (toDomain).
    val allEquipos: Flow<List<Equipo>> = equipoDao.getAllEquipos().map { entities ->
        entities.map { it.toDomain() }
    }

    // 'suspend' = puede tardar y solo se llama desde una corrutina. '?.toDomain()' = si no existe (null) devuelve
    //   null; si existe lo convierte.
    suspend fun getEquipoByCodigo(codigo: String): Equipo? {
        return equipoDao.getEquipoByCodigo(codigo)?.toDomain()
    }

    suspend fun getEquipoById(id: String): Equipo? {
        return equipoDao.getEquipoById(id)?.toDomain()
    }

    // Guarda o reemplaza un equipo (lo convierte a Entity antes).
    suspend fun insertEquipo(equipo: Equipo) {
        equipoDao.insertEquipo(EquipoEntity.fromDomain(equipo))
    }

    // 'Seed' = sembrar datos de ejemplo. Solo si la tabla está vacía (COUNT = 0) inserta 3 equipos de demostración,
    //   para que la app no arranque vacía.
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
            // Convierte cada Equipo a Entity y los inserta todos juntos.
            equipoDao.insertAll(initialEquipos.map { EquipoEntity.fromDomain(it) })
        }
    }
}
