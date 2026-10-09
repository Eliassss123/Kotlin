// ============================================================================
// ARCHIVO : data/repository/InstrumentoRepository.kt
// CAPA    : Repositorio (MVVM)
// RESUMEN : Gestión y trazabilidad de instrumentos y vencimientos de calibración (RN-11, RT-11).
// ============================================================================

package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.InstrumentoDao
import com.induztek.protocolos.data.local.InstrumentoEntity
import com.induztek.protocolos.model.Instrumento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InstrumentoRepository(
    private val instrumentoDao: InstrumentoDao
) {
    val allInstrumentos: Flow<List<Instrumento>> = instrumentoDao.getAllInstrumentos().map { entities ->
        entities.map { it.toDomain() }
    }

    val instrumentosActivos: Flow<List<Instrumento>> = instrumentoDao.getInstrumentosActivos().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getInstrumentoById(id: String): Instrumento? {
        return instrumentoDao.getInstrumentoById(id)?.toDomain()
    }

    suspend fun insertInstrumento(instrumento: Instrumento) {
        instrumentoDao.insertInstrumento(InstrumentoEntity.fromDomain(instrumento))
    }

    suspend fun actualizarCalibracion(id: String, nuevoVencimiento: String) {
        instrumentoDao.actualizarCalibracion(id, nuevoVencimiento)
    }

    suspend fun seedInitialDataIfEmpty() {
        if (instrumentoDao.getCount() == 0) {
            val initialInstrumentos = listOf(
                Instrumento(
                    id = "INST-001",
                    tipo = "Medidor de Resistencia de Aislamiento (Megóhmetro)",
                    marca = "Megger",
                    modelo = "MIT525",
                    numeroSerie = "SN-2024-551",
                    vencimientoCalibracion = "2026-12-31",
                    activo = true
                ),
                Instrumento(
                    id = "INST-002",
                    tipo = "Medidor de Relación de Transformación (TTR)",
                    marca = "Vanguard",
                    modelo = "ATRT-03 S2",
                    numeroSerie = "VNG-2023-889",
                    vencimientoCalibracion = "2026-11-15",
                    activo = true
                ),
                Instrumento(
                    id = "INST-003",
                    tipo = "Microhmómetro Digital de Contactos",
                    marca = "AEMC",
                    modelo = "6250",
                    numeroSerie = "AEM-2023-102",
                    vencimientoCalibracion = "2026-10-30",
                    activo = true
                ),
                Instrumento(
                    id = "INST-004",
                    tipo = "Equipo de Prueba y Calibración de Relés",
                    marca = "Omicron",
                    modelo = "CMC 356",
                    numeroSerie = "OMI-2024-331",
                    vencimientoCalibracion = "2027-02-28",
                    activo = true
                ),
                Instrumento(
                    id = "INST-005",
                    tipo = "Generador de Alta Tensión VLF",
                    marca = "HVA",
                    modelo = "HVA28 VLF Test Set",
                    numeroSerie = "HVA-2022-441",
                    vencimientoCalibracion = "2026-08-01", // Vencido para demostración de alerta RN-11
                    activo = true
                )
            )
            instrumentoDao.insertAll(initialInstrumentos.map { InstrumentoEntity.fromDomain(it) })
        }
    }
}
