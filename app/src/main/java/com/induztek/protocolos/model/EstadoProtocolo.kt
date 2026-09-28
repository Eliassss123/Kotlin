// ============================================================================
// ARCHIVO : model/EstadoProtocolo.kt
// CAPA    : Modelo (dominio)
// RESUMEN : los 3 estados posibles de un protocolo. Un 'enum' es una lista CERRADA de opciones:
//           no puede existir un cuarto estado por error de tipeo.
// ============================================================================

package com.induztek.protocolos.model

// Cada opción lleva un 'label' (el texto que se muestra al usuario). REGISTRADO_TERRENO es el nombre interno.
enum class EstadoProtocolo(val label: String) {
    REGISTRADO_TERRENO("Registrado en terreno"),
    FORMALIZADO("Formalizado"),
    FUERA_DE_RANGO("Fuera de rango");

    // companion object = funciones que pertenecen a la clase y no a un objeto (como 'static' en otros lenguajes).
    companion object {
        // Convierte el texto guardado en la base de datos (por ejemplo 'Formalizado') de vuelta al enum.
        fun fromLabel(label: String): EstadoProtocolo {
            // 'entries' = lista de todas las opciones. find busca la primera cuyo label coincida (sin distinguir
            //   mayúsculas).
            return entries.find { it.label.equals(label, ignoreCase = true) }
                // Operador Elvis '?:' = si no encontró nada, usa REGISTRADO_TERRENO como valor por defecto.
                ?: REGISTRADO_TERRENO
        }
    }
}
