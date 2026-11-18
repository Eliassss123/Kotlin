







package com.induztek.protocolos.model

enum class ClasificacionEquipo(val label: String) {
    PRIMARIO("Primario"),
    SECUNDARIO("Secundario")
}

enum class TipoEquipo(
    val nombre: String,
    val clasificacion: ClasificacionEquipo,
    val pruebasRequeridas: List<String>
) {
    TRANSFORMADOR(
        nombre = "Transformador de Poder",
        clasificacion = ClasificacionEquipo.PRIMARIO,
        pruebasRequeridas = listOf(
            "Resistencia de aislación",
            "Relación de transformación (TTR)",
            "Resistencia de devanados"
        )
    ),
    INTERRUPTOR(
        nombre = "Interruptor de Poder",
        clasificacion = ClasificacionEquipo.PRIMARIO,
        pruebasRequeridas = listOf(
            "Resistencia de aislación",
            "Resistencia de contactos"
        )
    ),
    CELDA_MT(
        nombre = "Celda de Media Tensión",
        clasificacion = ClasificacionEquipo.PRIMARIO,
        pruebasRequeridas = listOf(
            "Resistencia de aislación",
            "Resistencia de contactos"
        )
    ),
    CABLE_PODER(
        nombre = "Cable de Poder",
        clasificacion = ClasificacionEquipo.PRIMARIO,
        pruebasRequeridas = listOf(
            "Resistencia de aislación por fase"
        )
    ),
    RELE_PROTECCION(
        nombre = "Relé de Protección",
        clasificacion = ClasificacionEquipo.SECUNDARIO,
        pruebasRequeridas = listOf(
            "Prueba de operación (inyección secundaria)"
        )
    );

    companion object {
        fun fromNombre(nombre: String): TipoEquipo {
            return entries.find {
                it.nombre.equals(nombre, ignoreCase = true) ||
                it.name.equals(nombre, ignoreCase = true) ||
                nombre.contains(it.nombre, ignoreCase = true) ||
                (it == TRANSFORMADOR && (nombre.contains("Transformador", ignoreCase = true) || nombre.contains("TR", ignoreCase = true))) ||
                (it == INTERRUPTOR && (nombre.contains("Interruptor", ignoreCase = true) || nombre.contains("INT", ignoreCase = true))) ||
                (it == CELDA_MT && (nombre.contains("Celda", ignoreCase = true) || nombre.contains("CEL", ignoreCase = true))) ||
                (it == CABLE_PODER && (nombre.contains("Cable", ignoreCase = true) || nombre.contains("CAB", ignoreCase = true))) ||
                (it == RELE_PROTECCION && (nombre.contains("Relé", ignoreCase = true) || nombre.contains("Rele", ignoreCase = true) || nombre.contains("REL", ignoreCase = true)))
            } ?: TRANSFORMADOR
        }
    }
}
