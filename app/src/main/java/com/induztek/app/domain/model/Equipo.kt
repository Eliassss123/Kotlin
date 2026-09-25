package com.induztek.app.domain.model

/**
 * Modelo de dominio puro para un Equipo Eléctrico en faenas de Induztek Ingeniería SpA.
 */
data class Equipo(
    val id: Long = 0,
    val codigo: String,          // ej. "TRF-001", "REL-042", "PAT-101", "GEN-501"
    val tipo: TipoEquipo,
    val instalacion: String,     // Cliente o ubicación física en Chile
    val descripcion: String = ""
)

/**
 * Tipos de equipo eléctrico intervenidos en terreno según estándares SEC / IEEE.
 */
enum class TipoEquipo {
    TRANSFORMADOR,  // Transformadores de potencia y distribución (AT/MT)
    RELE,           // Relés de protección secundaria
    CELDA,          // Celdas de maniobra en media tensión
    CABLE,          // Cables subterráneos y alimentadores de fuerza
    MALLA_TIERRA,   // Sistema de Puesta a Tierra (SPT) según Pliego SEC RIC N°06
    GENERADOR,      // Grupos electrógenos y generadores de respaldo
    OTRO
}
