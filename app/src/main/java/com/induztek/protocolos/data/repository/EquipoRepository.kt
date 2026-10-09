// ============================================================================
// ARCHIVO : data/repository/EquipoRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Intermediario de equipos con soporte para datos de placa (FT-03 sec. 2),
//           detección de duplicados (RT-02) y filtros por empresa para cliente (P-04).
// ============================================================================

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

    fun getEquiposByCliente(cliente: String): Flow<List<Equipo>> {
        return equipoDao.getEquiposByCliente(cliente).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getEquipoByCodigo(codigo: String): Equipo? {
        return equipoDao.getEquipoByCodigo(codigo)?.toDomain()
    }

    suspend fun getEquipoById(id: String): Equipo? {
        return equipoDao.getEquipoById(id)?.toDomain()
    }

    // RT-02: Detectar duplicados en terreno (mismo TAG/serie en la misma instalación)
    suspend fun buscarPosibleDuplicado(ubicacion: String, tag: String, numeroSerie: String, codigo: String): Equipo? {
        return equipoDao.buscarPosibleDuplicado(ubicacion, tag, numeroSerie, codigo)?.toDomain()
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
                    tagCliente = "TAG-TR-01",
                    tipo = "Transformador de Poder",
                    cliente = "Planta Lampa",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    marca = "WEG / Tusán",
                    modelo = "TP-500-23",
                    numeroSerie = "SN-2018-9942",
                    potenciaTension = "500 kVA / 23 kV - 380 V",
                    anio = "2018",
                    tensiones = "23000 / 380 V",
                    tap = "Tap 3 (Normal)",
                    conexion = "Dyn11",
                    ultimoEstado = EstadoProtocolo.FORMALIZADO,
                    empeorando = false
                ),
                Equipo(
                    id = "EQ-002",
                    codigo = "REL-SEL751-02",
                    tagCliente = "TAG-REL-51P",
                    tipo = "Relé de Protección",
                    cliente = "Planta Lampa",
                    ubicacion = "Sala Eléctrica Planta Lampa - Alimentador 12kV",
                    marca = "Schweitzer (SEL)",
                    modelo = "SEL-751A",
                    numeroSerie = "SEL-2022-771",
                    potenciaTension = "Alimentación 125 VDC",
                    anio = "2022",
                    tensiones = "125 VDC / 5A Sec",
                    tap = "N/A",
                    conexion = "Secundaria",
                    ultimoEstado = EstadoProtocolo.REGISTRADO_TERRENO,
                    empeorando = false
                ),
                Equipo(
                    id = "EQ-003",
                    codigo = "CEL-MT24-03",
                    tagCliente = "TAG-CEL-03",
                    tipo = "Celda de Media Tensión",
                    cliente = "Agroindustria Rancagua",
                    ubicacion = "Subestación Distribución Sur",
                    marca = "Schneider Electric",
                    modelo = "SM6-24kV",
                    numeroSerie = "SCH-MT-2019-11",
                    potenciaTension = "24 kV / 630 A / 16 kA",
                    anio = "2019",
                    tensiones = "24 kV",
                    tap = "N/A",
                    conexion = "Barras 3P",
                    ultimoEstado = EstadoProtocolo.OBSERVADO,
                    // RT-07: Equipo empeorando (dos años aprobado y este año variación alta / observado)
                    empeorando = true
                ),
                Equipo(
                    id = "EQ-004",
                    codigo = "INT-VAC24-04",
                    tagCliente = "TAG-INT-01",
                    tipo = "Interruptor de Poder",
                    cliente = "Centro de Datos Titanium",
                    ubicacion = "Sala UPS Principal - Acometida 1",
                    marca = "Eaton",
                    modelo = "VCP-W 24kV",
                    numeroSerie = "ETN-2021-508",
                    potenciaTension = "24 kV / 1250 A / 25 kA",
                    anio = "2021",
                    tensiones = "24 kV",
                    tap = "N/A",
                    conexion = "Extraíble",
                    ultimoEstado = EstadoProtocolo.FORMALIZADO,
                    empeorando = false
                ),
                Equipo(
                    id = "EQ-005",
                    codigo = "CAB-XLPE-05",
                    tagCliente = "TAG-CAB-MT1",
                    tipo = "Cable de Poder",
                    cliente = "Clínica Providencia",
                    ubicacion = "Túnel de Cables Subterráneo Acometida B",
                    marca = "Nexans",
                    modelo = "XLPE 15/25 kV 3x120 mm²",
                    numeroSerie = "NX-2020-L800",
                    potenciaTension = "25 kV / 120 mm² Cu",
                    anio = "2020",
                    tensiones = "15/25 kV",
                    tap = "N/A",
                    conexion = "Terminales termocontraíbles",
                    ultimoEstado = EstadoProtocolo.REGISTRADO_TERRENO,
                    empeorando = false
                )
            )
            equipoDao.insertAll(initialEquipos.map { EquipoEntity.fromDomain(it) })
        }
    }
}
