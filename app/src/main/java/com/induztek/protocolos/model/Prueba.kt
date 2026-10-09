// ============================================================================
// ARCHIVO : model/Prueba.kt
// CAPA    : Modelo (dominio)
// RESUMEN : Ensayo o prueba individual según RN-05, RN-06/07/08/08b/08c, RN-09, RN-11 y Aclaración 2:
//           - Estado REALIZADA o NO_REALIZADA (con motivo no vacío obligatorio)
//           - Resultado Aprobado, Observado, Rechazado (calculado o asignado a mano en devanados)
//           - Trazabilidad de instrumento (serie y calibración)
//           - Variación histórica con respecto a la prueba anterior del mismo tipo (>25% destacada)
// ============================================================================

package com.induztek.protocolos.model

import java.util.UUID

data class Prueba(
    val id: String = UUID.randomUUID().toString(),
    val protocoloId: String = "",
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String = "",
    val tipoPrueba: String,
    val ubicacion: String = "",
    val fechaHora: String = "",
    val estadoPrueba: EstadoPrueba = EstadoPrueba.REALIZADA,
    val motivoNoRealizada: String = "",
    val resultado: ResultadoPrueba = ResultadoPrueba.APROBADO,
    val instrumentoId: String = "",
    val instrumento: String = "Megger MIT525",
    val instrumentoSerie: String = "SN-2024-551",
    val instrumentoCalibracion: String = "2026-12-31",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val mediciones: List<Medicion> = emptyList(),
    val observaciones: String = "",
    val variacionHistoricaPorcentaje: Double? = null,
    val superaVariacion25: Boolean = false
) {
    // Compatibilidad con vistas previas
    val estado: EstadoProtocolo
        get() = when (resultado) {
            ResultadoPrueba.APROBADO -> EstadoProtocolo.FORMALIZADO
            ResultadoPrueba.OBSERVADO -> EstadoProtocolo.REGISTRADO_TERRENO
            ResultadoPrueba.RECHAZADO -> EstadoProtocolo.OBSERVADO
            ResultadoPrueba.NO_REALIZADA -> EstadoProtocolo.BORRADOR
        }
}
