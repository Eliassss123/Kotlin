package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.local.PruebaDao
import com.induztek.protocolos.data.local.PruebaEntity
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Prueba
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

    val allPruebas: Flow<List<Prueba>> = pruebaDao.getAllPruebas().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertPrueba(prueba: Prueba) {
        pruebaDao.insertPrueba(PruebaEntity.fromDomain(prueba))
    }

    suspend fun seedInitialDataIfEmpty() {
        if (pruebaDao.getCount() == 0) {
            val initialPruebas = listOf(
                // Equipo 1: TR-500KVA-01
                Prueba(
                    id = "PRU-101",
                    equipoCodigo = "TR-500KVA-01",
                    tipoPrueba = "Resistencia de Aislamiento (Megger)",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    fechaHora = "2026-08-15 10:30",
                    mediciones = listOf(
                        Medicion("Resistencia Alta-Tierra (1min)", "4.5 GΩ"),
                        Medicion("Resistencia Baja-Tierra (1min)", "3.8 GΩ"),
                        Medicion("Índice de Polarización (IP)", "1.85"),
                        Medicion("Tensión Aplicada", "2500 V")
                    ),
                    estado = EstadoProtocolo.FORMALIZADO,
                    observaciones = "Valores dentro de norma IEEE 43. Operación normal aprobada."
                ),
                Prueba(
                    id = "PRU-102",
                    equipoCodigo = "TR-500KVA-01",
                    tipoPrueba = "Relación de Transformación (TTR)",
                    ubicacion = "Subestación Principal Norte - Celda 01",
                    fechaHora = "2026-09-10 14:15",
                    mediciones = listOf(
                        Medicion("Relación Fase H1-H2/X1-X2", "21.74"),
                        Medicion("Error Relación %", "0.12%"),
                        Medicion("Desplazamiento Angular", "0.0°")
                    ),
                    estado = EstadoProtocolo.FORMALIZADO,
                    observaciones = "Verificación periódica semestral sin desviaciones."
                ),
                // Equipo 2: REL-SEL751-02
                Prueba(
                    id = "PRU-201",
                    equipoCodigo = "REL-SEL751-02",
                    tipoPrueba = "Inyección Secundaria de Corriente 51P",
                    ubicacion = "Planta Lampa - Alimentador 12kV",
                    fechaHora = "2026-09-01 11:00",
                    mediciones = listOf(
                        Medicion("Corriente Recogida (I_pickup)", "5.02 A"),
                        Medicion("Tiempo Disparo (1.5x I_pickup)", "0.42 s"),
                        Medicion("Tensión Auxiliar DC", "125 V")
                    ),
                    estado = EstadoProtocolo.REGISTRADO_TERRENO,
                    observaciones = "Prueba funcional en terreno ok. Pendiente validación de curva por jefe de área."
                ),
                Prueba(
                    id = "PRU-202",
                    equipoCodigo = "REL-SEL751-02",
                    tipoPrueba = "Verificación Entradas/Salidas Digitales",
                    ubicacion = "Planta Lampa - Alimentador 12kV",
                    fechaHora = "2026-09-18 09:45",
                    mediciones = listOf(
                        Medicion("Entrada IN101 Trip Externo", "Activo"),
                        Medicion("Salida OUT101 Disparo Interruptor", "Conmutó ok"),
                        Medicion("Tiempo de Respuesta Contacto", "12 ms")
                    ),
                    estado = EstadoProtocolo.REGISTRADO_TERRENO,
                    observaciones = "Interbloqueos probados satisfactoriamente."
                ),
                // Equipo 3: CEL-MT24-03
                Prueba(
                    id = "PRU-301",
                    equipoCodigo = "CEL-MT24-03",
                    tipoPrueba = "Resistencia de Contacto (Microhmómetro)",
                    ubicacion = "Subestación Distribución Sur",
                    fechaHora = "2026-08-20 16:20",
                    mediciones = listOf(
                        Medicion("Resistencia Contacto Fase R", "45 µΩ"),
                        Medicion("Resistencia Contacto Fase S", "120 µΩ"),
                        Medicion("Resistencia Contacto Fase T", "42 µΩ")
                    ),
                    estado = EstadoProtocolo.FUERA_DE_RANGO,
                    observaciones = "Fase S supera límite máximo permitido (80 µΩ). Requiere mantenimiento urgente en tulipa de contacto."
                ),
                Prueba(
                    id = "PRU-302",
                    equipoCodigo = "CEL-MT24-03",
                    tipoPrueba = "Hipot VLF en Cables de MT",
                    ubicacion = "Subestación Distribución Sur",
                    fechaHora = "2026-09-22 15:00",
                    mediciones = listOf(
                        Medicion("Tensión VLF Aplicada (0.1 Hz)", "18 kV"),
                        Medicion("Fuga Fase R (15 min)", "1.2 mA"),
                        Medicion("Fuga Fase S (15 min)", "4.8 mA"),
                        Medicion("Fuga Fase T (15 min)", "1.1 mA")
                    ),
                    estado = EstadoProtocolo.FUERA_DE_RANGO,
                    observaciones = "Fase S presentó corriente de fuga elevada y descarga parcial recurrente."
                )
            )
            pruebaDao.insertAll(initialPruebas.map { PruebaEntity.fromDomain(it) })
        }
    }
}
