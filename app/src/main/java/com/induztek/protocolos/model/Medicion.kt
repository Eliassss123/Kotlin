






package com.induztek.protocolos.model

import java.util.UUID

data class Medicion(
    val id: String = UUID.randomUUID().toString(),
    val campo: String,
    val valorNumerico: Double?,
    val unidad: String,
    val momento: MomentoMedicion = MomentoMedicion.PREVIA,
    val instrumentoInfo: String = ""
) {
    
    val valorFormateado: String
        get() = if (valorNumerico != null) {
            val valorTxt = if (valorNumerico % 1.0 == 0.0) {
                valorNumerico.toLong().toString()
            } else {
                String.format(java.util.Locale.US, "%.2f", valorNumerico)
            }
            if (unidad.isNotBlank()) "$valorTxt $unidad" else valorTxt
        } else {
            "-"
        }

    
    constructor(campo: String, valorTexto: String) : this(
        campo = campo,
        valorNumerico = parseValorNumerico(valorTexto),
        unidad = extractUnidad(valorTexto),
        momento = MomentoMedicion.PREVIA
    )

    companion object {
        fun parseValorNumerico(texto: String): Double? {
            val clean = texto.replace(",", ".").trim()
            val match = Regex("[-+]?[0-9]*\\.?[0-9]+").find(clean)
            return match?.value?.toDoubleOrNull()
        }

        fun extractUnidad(texto: String): String {
            val numStr = parseValorNumerico(texto)
            if (numStr == null) return ""
            val clean = texto.replace(",", ".").trim()
            val match = Regex("[-+]?[0-9]*\\.?[0-9]+").find(clean) ?: return ""
            val unidad = clean.substring(match.range.last + 1).trim()
            return unidad
        }
    }
}
