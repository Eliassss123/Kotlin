package com.induztek.protocolos.model

enum class EstadoProtocolo(val label: String) {
    REGISTRADO_TERRENO("Registrado en terreno"),
    FORMALIZADO("Formalizado"),
    FUERA_DE_RANGO("Fuera de rango");

    companion object {
        fun fromLabel(label: String): EstadoProtocolo {
            return entries.find { it.label.equals(label, ignoreCase = true) }
                ?: REGISTRADO_TERRENO
        }
    }
}
