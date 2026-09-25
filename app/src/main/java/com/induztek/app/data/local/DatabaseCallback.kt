package com.induztek.app.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.induztek.app.data.local.dao.EquipoDao
import com.induztek.app.data.local.dao.PruebaDao
import com.induztek.app.data.local.entity.EquipoEntity
import com.induztek.app.data.local.entity.PruebaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

/**
 * Callback de inicialización con datos sintéticos realistas de ingeniería eléctrica chilena (Induztek SpA).
 */
class DatabaseCallback(
    private val equipoDaoProvider: Provider<EquipoDao>,
    private val pruebaDaoProvider: Provider<PruebaDao>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            val equipoDao = equipoDaoProvider.get()
            val pruebaDao = pruebaDaoProvider.get()

            seedEquiposYPruebas(equipoDao, pruebaDao)
        }
    }

    private suspend fun seedEquiposYPruebas(equipoDao: EquipoDao, pruebaDao: PruebaDao) {
        val equiposSeed = listOf(
            EquipoEntity(
                id          = 1L,
                codigo      = "TRF-001",
                tipo        = "TRANSFORMADOR",
                instalacion = "Subestación Norte — Cliente: Minera Atacama S.A.",
                descripcion = "Transformador trifásico 10 MVA, 33/0.4 kV (Aceite mineral)"
            ),
            EquipoEntity(
                id          = 2L,
                codigo      = "TRF-002",
                tipo        = "TRANSFORMADOR",
                instalacion = "Planta Industrial Este — Cliente: Procesadora Litio Ltda.",
                descripcion = "Transformador seco encapsulado 2.5 MVA, 13.2/0.22 kV"
            ),
            EquipoEntity(
                id          = 3L,
                codigo      = "REL-042",
                tipo        = "RELE",
                instalacion = "Subestación Norte — Cliente: Minera Atacama S.A.",
                descripcion = "Relé de sobrecorriente y tierra Schneider SEPAM 40"
            ),
            EquipoEntity(
                id          = 4L,
                codigo      = "CEL-010",
                tipo        = "CELDA",
                instalacion = "Subestación Norte — Cliente: Minera Atacama S.A.",
                descripcion = "Celda MT 24 kV llegada, interruptor tipo vacío"
            ),
            EquipoEntity(
                id          = 5L,
                codigo      = "CAB-201",
                tipo        = "CABLE",
                instalacion = "Planta Industrial Este — Cliente: Procesadora Litio Ltda.",
                descripcion = "Alimentador subterráneo 3x240 mm² XLPE 15 kV, 850 m"
            ),
            EquipoEntity(
                id          = 6L,
                codigo      = "PAT-101",
                tipo        = "MALLA_TIERRA",
                instalacion = "Subestación Norte — Cliente: Minera Atacama S.A.",
                descripcion = "Malla de puesta a tierra principal 40x30m (Cobre desnudo 4/0 AWG)"
            ),
            EquipoEntity(
                id          = 7L,
                codigo      = "GEN-501",
                tipo        = "GENERADOR",
                instalacion = "Edificio Corporativo — Cliente: Datacenter Santiago Sur",
                descripcion = "Grupo electrógeno diésel Caterpillar 500 kVA / 400V de respaldo crítico"
            )
        )
        equiposSeed.forEach { equipoDao.insert(it) }

        // Pruebas históricas sintéticas con mediciones reales de ingeniería
        val gson = Gson()
        val pruebasSeed = listOf(
            PruebaEntity(
                id             = 1L,
                equipoId       = 1L, // TRF-001
                fecha          = "2024-03-15",
                hora           = "10:30",
                tecnico        = "Ing. Carlos Mendoza",
                medicionesJson = gson.toJson(mapOf(
                    "resistencia_aislamiento" to "1200 MΩ",
                    "tension_prueba" to "2.5 kV",
                    "temperatura" to "21 °C"
                )),
                observaciones  = "Mantenimiento preventivo anual. Parámetros dentro de norma IEEE 43.",
                estado         = "FORMALIZADO",
                sincronizadoAt = "2024-03-15T15:00:00Z"
            ),
            PruebaEntity(
                id             = 2L,
                equipoId       = 1L, // TRF-001
                fecha          = "2025-03-20",
                hora           = "11:15",
                tecnico        = "Ing. Carlos Mendoza",
                medicionesJson = gson.toJson(mapOf(
                    "resistencia_aislamiento" to "1150 MΩ",
                    "tension_prueba" to "2.5 kV",
                    "temperatura" to "23 °C"
                )),
                observaciones  = "Aislamiento estable respecto a 2024. Sin degradación térmica.",
                estado         = "FORMALIZADO",
                sincronizadoAt = "2025-03-20T16:20:00Z"
            ),
            PruebaEntity(
                id             = 3L,
                equipoId       = 3L, // REL-042
                fecha          = "2025-08-10",
                hora           = "16:45",
                tecnico        = "Ing. Carlos Mendoza",
                medicionesJson = gson.toJson(mapOf(
                    "tiempo_disparo" to "42 ms",
                    "corriente_pickup" to "5.1 A",
                    "tension_control" to "110 V"
                )),
                observaciones  = "Curva de protección ANSI 50/51 verificada con maleta de inyección Omicron.",
                estado         = "FORMALIZADO",
                sincronizadoAt = "2025-08-10T18:00:00Z"
            ),
            PruebaEntity(
                id             = 4L,
                equipoId       = 6L, // PAT-101 (Malla Tierra)
                fecha          = "2025-01-18",
                hora           = "09:00",
                tecnico        = "Ing. Carlos Mendoza",
                medicionesJson = gson.toJson(mapOf(
                    "resistencia_tierra" to "1.8 Ω",
                    "metodo_medicion" to "Caída de Potencial (3 Picas)",
                    "temperatura" to "26 °C"
                )),
                observaciones  = "Conforme a Pliego SEC RIC N°06 (exigencia < 5.0 Ω en subestaciones).",
                estado         = "FORMALIZADO",
                sincronizadoAt = "2025-01-18T14:30:00Z"
            )
        )
        pruebasSeed.forEach { pruebaDao.insert(it) }
    }
}
