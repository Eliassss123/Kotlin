





package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.PruebaDao
import com.induztek.protocolos.data.local.PruebaEntity
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PruebaRepository(
    private val pruebaDao: PruebaDao
) {
    
    fun getPruebasByEquipo(equipoCodigo: String): Flow<List<Prueba>> {
        return pruebaDao.getPruebasByEquipo(equipoCodigo).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    
    fun getPruebasByProtocolo(protocoloId: String): Flow<List<Prueba>> {
        return pruebaDao.getPruebasByProtocolo(protocoloId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    
    suspend fun getPruebaById(id: String): Prueba? {
        return pruebaDao.getPruebaById(id)?.toDomain()
    }

    
    suspend fun getUltimaPruebaPorTipo(equipoCodigo: String, tipoPrueba: String): Prueba? {
        return pruebaDao.getUltimaPruebaPorTipo(equipoCodigo, tipoPrueba)?.toDomain()
    }

    
    val allPruebas: Flow<List<Prueba>> = pruebaDao.getAllPruebas().map { entities ->
        entities.map { it.toDomain() }
    }

    
    suspend fun insertPrueba(prueba: Prueba) {
        pruebaDao.insertPrueba(PruebaEntity.fromDomain(prueba))
    }

    
    suspend fun seedInitialDataIfEmpty() {
        if (pruebaDao.getCount() == 0) {
            val initialPruebas = listOf(
                Prueba(
                    id = "PRU-101",
                    protocoloId = "PROT-2026-001",
                    ordenTrabajo = "OT-2026-081",
                    equipoCodigo = "TR-500KVA-01",
                    tipoPrueba = "Resistencia de aislación",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    fechaHora = "2026-08-15 10:30",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = "Megger MIT525",
                    instrumentoSerie = "SN-2024-551",
                    instrumentoCalibracion = "2026-12-31",
                    calibracionVigente = true,
                    temperaturaAmbiente = "21°C",
                    humedad = "42%",
                    mediciones = listOf(
                        Medicion(campo = "Lectura 1 min (R1min)", valorNumerico = 4.5, unidad = "GΩ"),
                        Medicion(campo = "Lectura 10 min (R10min)", valorNumerico = 8.3, unidad = "GΩ"),
                        Medicion(campo = "Índice de Polarización (IP)", valorNumerico = 1.84, unidad = "ratio")
                    ),
                    observaciones = "Prueba de aislamiento inicial aprobada bajo norma IEEE 43.",
                    variacionHistoricaPorcentaje = 0.0,
                    superaVariacion25 = false
                ),
                Prueba(
                    id = "PRU-102",
                    protocoloId = "PROT-2026-001",
                    ordenTrabajo = "OT-2026-081",
                    equipoCodigo = "TR-500KVA-01",
                    tipoPrueba = "Relación de transformación (TTR)",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    fechaHora = "2026-08-15 11:15",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = "Vanguard ATRT-03 S2",
                    instrumentoSerie = "VNG-2023-889",
                    instrumentoCalibracion = "2026-11-15",
                    calibracionVigente = true,
                    temperaturaAmbiente = "21°C",
                    humedad = "42%",
                    mediciones = listOf(
                        Medicion(campo = "Relación Teórica (Placa)", valorNumerico = 21.74, unidad = "ratio"),
                        Medicion(campo = "Relación Medida (Fase U)", valorNumerico = 21.76, unidad = "ratio"),
                        Medicion(campo = "Relación Medida (Fase V)", valorNumerico = 21.75, unidad = "ratio"),
                        Medicion(campo = "Relación Medida (Fase W)", valorNumerico = 21.74, unidad = "ratio"),
                        Medicion(campo = "Error Máximo %", valorNumerico = 0.09, unidad = "%")
                    ),
                    observaciones = "Relación de transformación dentro de tolerancia <= 0.5%.",
                    variacionHistoricaPorcentaje = 0.0,
                    superaVariacion25 = false
                ),
                Prueba(
                    id = "PRU-103",
                    protocoloId = "PROT-2026-001",
                    ordenTrabajo = "OT-2026-081",
                    equipoCodigo = "TR-500KVA-01",
                    tipoPrueba = "Resistencia de devanados",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    fechaHora = "2026-08-15 11:45",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = "AEMC 6250",
                    instrumentoSerie = "AEM-2023-102",
                    instrumentoCalibracion = "2026-10-30",
                    calibracionVigente = true,
                    temperaturaAmbiente = "21°C",
                    humedad = "42%",
                    mediciones = listOf(
                        Medicion(campo = "Devanado AT Fase U-V", valorNumerico = 1.25, unidad = "Ω"),
                        Medicion(campo = "Devanado AT Fase V-W", valorNumerico = 1.26, unidad = "Ω"),
                        Medicion(campo = "Devanado AT Fase W-U", valorNumerico = 1.24, unidad = "Ω")
                    ),
                    observaciones = "Equilibrio resistivo trifásico conforme. Asignado por Ingeniero.",
                    variacionHistoricaPorcentaje = 0.0,
                    superaVariacion25 = false
                ),
                Prueba(
                    id = "PRU-201",
                    protocoloId = "PROT-2026-002",
                    ordenTrabajo = "OT-2026-082",
                    equipoCodigo = "REL-SEL751-02",
                    tipoPrueba = "Prueba de operación (inyección secundaria)",
                    ubicacion = "Planta Lampa - Alimentador 12kV",
                    fechaHora = "2026-09-01 11:00",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = "Omicron CMC 356",
                    instrumentoSerie = "OMI-2024-331",
                    instrumentoCalibracion = "2027-02-28",
                    calibracionVigente = true,
                    temperaturaAmbiente = "23°C",
                    humedad = "40%",
                    mediciones = listOf(
                        Medicion(campo = "Corriente Inyectada (I_inj)", valorNumerico = 5.02, unidad = "A"),
                        Medicion(campo = "Tiempo Teórico de Curva", valorNumerico = 0.40, unidad = "s"),
                        Medicion(campo = "Tiempo Medido de Disparo", valorNumerico = 0.41, unidad = "s"),
                        Medicion(campo = "Error Tiempo Disparo %", valorNumerico = 2.5, unidad = "%")
                    ),
                    observaciones = "Curva de sobrecorriente 51P verificada satisfactoriamente.",
                    variacionHistoricaPorcentaje = 0.0,
                    superaVariacion25 = false
                ),
                Prueba(
                    id = "PRU-301",
                    protocoloId = "PROT-2026-003",
                    ordenTrabajo = "OT-2026-083",
                    equipoCodigo = "CEL-MT24-03",
                    tipoPrueba = "Resistencia de aislación",
                    ubicacion = "Subestación Distribución Sur",
                    fechaHora = "2026-08-20 16:00",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.APROBADO,
                    instrumento = "Megger MIT525",
                    instrumentoSerie = "SN-2024-551",
                    instrumentoCalibracion = "2026-12-31",
                    calibracionVigente = true,
                    temperaturaAmbiente = "25°C",
                    humedad = "38%",
                    mediciones = listOf(
                        Medicion(campo = "Lectura 1 min (R1min)", valorNumerico = 5.2, unidad = "GΩ"),
                        Medicion(campo = "Lectura 10 min (R10min)", valorNumerico = 10.8, unidad = "GΩ"),
                        Medicion(campo = "Índice de Polarización (IP)", valorNumerico = 2.07, unidad = "ratio")
                    ),
                    observaciones = "Aislamiento de barras conforme.",
                    variacionHistoricaPorcentaje = 0.0,
                    superaVariacion25 = false
                ),
                Prueba(
                    id = "PRU-302",
                    protocoloId = "PROT-2026-003",
                    ordenTrabajo = "OT-2026-083",
                    equipoCodigo = "CEL-MT24-03",
                    tipoPrueba = "Resistencia de contactos",
                    ubicacion = "Subestación Distribución Sur",
                    fechaHora = "2026-08-20 16:30",
                    estadoPrueba = EstadoPrueba.REALIZADA,
                    motivoNoRealizada = "",
                    resultado = ResultadoPrueba.OBSERVADO,
                    instrumento = "AEMC 6250",
                    instrumentoSerie = "AEM-2023-102",
                    instrumentoCalibracion = "2026-10-30",
                    calibracionVigente = true,
                    temperaturaAmbiente = "25°C",
                    humedad = "38%",
                    mediciones = listOf(
                        Medicion(campo = "Resistencia Polo / Fase R", valorNumerico = 45.0, unidad = "µΩ"),
                        Medicion(campo = "Resistencia Polo / Fase S", valorNumerico = 85.0, unidad = "µΩ"),
                        Medicion(campo = "Resistencia Polo / Fase T", valorNumerico = 42.0, unidad = "µΩ")
                    ),
                    observaciones = "Fase S presentó 85 µΩ (Observado, rango 50-100 µΩ). Variación alta respecto al año anterior (+45%).",
                    variacionHistoricaPorcentaje = 45.0,
                    superaVariacion25 = true
                )
            )
            pruebaDao.insertAll(initialPruebas.map { PruebaEntity.fromDomain(it) })
        }
    }
}
