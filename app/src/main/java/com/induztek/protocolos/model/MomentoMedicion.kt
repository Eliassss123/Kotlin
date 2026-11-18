







package com.induztek.protocolos.model

enum class MomentoMedicion(val label: String) {
    PREVIA("Medición Previa"),
    POSTERIOR_MANTENIMIENTO("Posterior a Mantenimiento");

    companion object {
        fun fromLabel(label: String): MomentoMedicion {
            return entries.find {
                it.label.equals(label, ignoreCase = true) ||
                it.name.equals(label, ignoreCase = true) ||
                (label.contains("Post", ignoreCase = true) && it == POSTERIOR_MANTENIMIENTO)
            } ?: PREVIA
        }
    }
}
