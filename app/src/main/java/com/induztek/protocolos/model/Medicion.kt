// ============================================================================
// ARCHIVO : model/Medicion.kt
// CAPA    : Modelo (dominio)
// RESUMEN : una medición individual: un parámetro y su valor (p. ej. "Resistencia Alta-Tierra" = "4.5 GOhm").
//           El valor es texto libre; la app no lo interpreta como número.
// ============================================================================

package com.induztek.protocolos.model

// campo = nombre del parámetro; valor = lo medido con su unidad.
data class Medicion(
    val campo: String,
    val valor: String
)
