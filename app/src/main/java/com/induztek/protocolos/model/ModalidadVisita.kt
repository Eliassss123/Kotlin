







package com.induztek.protocolos.model

enum class ModalidadVisita(val label: String, val descripcion: String) {
    SOLO_PRUEBAS(
        label = "Solo Pruebas",
        descripcion = "Ejecución de ensayos eléctricos rutinarios"
    ),
    PRUEBAS_Y_MANTENIMIENTO(
        label = "Pruebas y Mantenimiento",
        descripcion = "Pruebas previas, intervención de mantenimiento y pruebas posteriores"
    );

    companion object {
        fun fromLabel(label: String): ModalidadVisita {
            return entries.find {
                it.label.equals(label, ignoreCase = true) ||
                it.name.equals(label, ignoreCase = true) ||
                (label.contains("Mantenimiento", ignoreCase = true) && it == PRUEBAS_Y_MANTENIMIENTO)
            } ?: SOLO_PRUEBAS
        }
    }
}
