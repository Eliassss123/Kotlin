








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
    val vencimientoCalibracion: String, 
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
            true 
        }
    }
}
