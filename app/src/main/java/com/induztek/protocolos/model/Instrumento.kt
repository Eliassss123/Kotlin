// ============================================================================
// ARCHIVO : model/Instrumento.kt
// CAPA    : Modelo (dominio)
// RESUMEN : Instrumento de medición según RN-11, RT-11 y Anexo 12 (FT-03):
//           - Tipo, marca, modelo, número de serie, fecha de vencimiento de calibración
//           - Gestionado por Jefe Técnico (P-03).
//           - Validar o alertar si la calibración está vencida al seleccionar el instrumento.
// ============================================================================

package com.induztek.protocolos.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Instrumento(
    val id: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val numeroSerie: String,
    val vencimientoCalibracion: String, // Formato YYYY-MM-DD
    val activo: Boolean = true
) {
    val descripcionCompleta: String
        get() = "$marca $modelo (S/N: $numeroSerie)"

    fun estaCalibracionVigente(): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaVenc = sdf.parse(vencimientoCalibracion) ?: return false
            val hoy = Date()
            !fechaVenc.before(hoy)
        } catch (e: Exception) {
            true // Fallback seguro
        }
    }
}
