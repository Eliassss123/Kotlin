// ============================================================================
// ARCHIVO : model/Prueba.kt
// CAPA    : Modelo (dominio)
// RESUMEN : una prueba técnica hecha a un equipo (aparece en el historial del detalle de equipo).
//           Parecida a Protocolo, pero sin técnico ni código de protocolo.
// ============================================================================

package com.induztek.protocolos.model

// tipoPrueba = nombre de la prueba (Megger, TTR, Hipot...).
data class Prueba(
    val id: String,
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    val mediciones: List<Medicion>,
    val estado: EstadoProtocolo,
    val observaciones: String
)
