








package com.induztek.protocolos.model

import java.util.UUID

data class Equipo(
    val id: String = UUID.randomUUID().toString(),
    
    val codigo: String,
    
    val tagCliente: String = "",
    
    val tipo: String,
    
    val cliente: String = "",
    
    val ubicacion: String,
    
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val potenciaTension: String = "",
    val anio: String = "2020",
    val tensiones: String = "23 kV / 380 V",
    val tap: String = "3",
    val conexion: String = "Dyn11",
    
    val ultimoEstado: EstadoProtocolo = EstadoProtocolo.REGISTRADO_TERRENO,
    
    val empeorando: Boolean = false
) {
    val clasificacion: ClasificacionEquipo
        get() = TipoEquipo.fromNombre(tipo).clasificacion

    
    fun getTapEtiqueta(): String {
        val t = tap.trim()
        val num = t.filter { it.isDigit() }.toIntOrNull() ?: t.toIntOrNull()
        return when {
            num == 3 -> "$num (Nominal)"
            num != null -> "$num"
            t.isNotBlank() && t != "N/A" -> t
            else -> "N/A"
        }
    }
}
