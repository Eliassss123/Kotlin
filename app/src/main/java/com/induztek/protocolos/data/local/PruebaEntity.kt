// ============================================================================
// ARCHIVO : data/local/PruebaEntity.kt
// CAPA    : Datos locales (Room)
// RESUMEN : Tabla 'pruebas' según RN-05, RN-06/07/08/08b/08c, RN-09, RN-11 y Aclaración 2:
//           - Estado REALIZADA / NO_REALIZADA y motivo obligatorio
//           - Resultado y serie/calibración de instrumento
//           - Mediciones con momentos e indicador de variación histórica (>25%)
// ============================================================================

package com.induztek.protocolos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba

/**
 * Entidad de Room que representa la tabla 'pruebas' en la base de datos local.
 * Almacena los resultados de cada prueba individual, instrumentos utilizados, mediciones serializadas y variaciones históricas.
 */
@Entity(tableName = "pruebas")
data class PruebaEntity(
    @PrimaryKey val id: String,
    val protocoloId: String = "",
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    val estadoPrueba: String = "Realizada",
    val motivoNoRealizada: String = "",
    val resultado: String = "Aprobado",
    val instrumentoId: String = "",
    val instrumento: String = "Megger MIT525",
    val instrumentoSerie: String = "SN-2024-551",
    val instrumentoCalibracion: String = "2026-12-31",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val medicionesJson: String,
    val observaciones: String,
    val variacionHistoricaPorcentaje: Double? = null,
    val superaVariacion25: Boolean = false
) {
    /**
     * Convierte la entidad [PruebaEntity] al modelo de dominio [Prueba], deserializando las mediciones desde JSON.
     */
    fun toDomain(): Prueba {
        val listType = object : TypeToken<List<Medicion>>() {}.type
        val medicionesList: List<Medicion> = try {
            Gson().fromJson(medicionesJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Prueba(
            id = id,
            protocoloId = protocoloId,
            ordenTrabajo = ordenTrabajo,
            equipoCodigo = equipoCodigo,
            tipoPrueba = tipoPrueba,
            ubicacion = ubicacion,
            fechaHora = fechaHora,
            estadoPrueba = EstadoPrueba.fromLabel(estadoPrueba),
            motivoNoRealizada = motivoNoRealizada,
            resultado = ResultadoPrueba.fromLabel(resultado),
            instrumentoId = instrumentoId,
            instrumento = instrumento,
            instrumentoSerie = instrumentoSerie,
            instrumentoCalibracion = instrumentoCalibracion,
            calibracionVigente = calibracionVigente,
            temperaturaAmbiente = temperaturaAmbiente,
            humedad = humedad,
            mediciones = medicionesList,
            observaciones = observaciones,
            variacionHistoricaPorcentaje = variacionHistoricaPorcentaje,
            superaVariacion25 = superaVariacion25
        )
    }

    companion object {
        /**
         * Crea una instancia de [PruebaEntity] a partir del modelo de dominio [Prueba], serializando las mediciones a JSON.
         */
        fun fromDomain(prueba: Prueba): PruebaEntity {
            return PruebaEntity(
                id = prueba.id,
                protocoloId = prueba.protocoloId,
                ordenTrabajo = prueba.ordenTrabajo,
                equipoCodigo = prueba.equipoCodigo,
                tipoPrueba = prueba.tipoPrueba,
                ubicacion = prueba.ubicacion,
                fechaHora = prueba.fechaHora,
                estadoPrueba = prueba.estadoPrueba.label,
                motivoNoRealizada = prueba.motivoNoRealizada,
                resultado = prueba.resultado.label,
                instrumentoId = prueba.instrumentoId,
                instrumento = prueba.instrumento,
                instrumentoSerie = prueba.instrumentoSerie,
                instrumentoCalibracion = prueba.instrumentoCalibracion,
                calibracionVigente = prueba.calibracionVigente,
                temperaturaAmbiente = prueba.temperaturaAmbiente,
                humedad = prueba.humedad,
                medicionesJson = Gson().toJson(prueba.mediciones),
                observaciones = prueba.observaciones,
                variacionHistoricaPorcentaje = prueba.variacionHistoricaPorcentaje,
                superaVariacion25 = prueba.superaVariacion25
            )
        }
    }
}
