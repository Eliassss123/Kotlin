










package com.induztek.protocolos.model

enum class EstadoProtocolo(
    val label: String,
    val esEditablePorTecnico: Boolean,
    val esInmutable: Boolean
) {
    BORRADOR("Borrador", esEditablePorTecnico = true, esInmutable = false),
    OBSERVADO("Observado", esEditablePorTecnico = true, esInmutable = false),
    REGISTRADO_TERRENO("Registrado en terreno", esEditablePorTecnico = false, esInmutable = false),
    FORMALIZADO("Formalizado", esEditablePorTecnico = false, esInmutable = true);

    fun getLabelCliente(esCliente: Boolean): String {
        return if (esCliente && this != FORMALIZADO) {
            "Pendiente de formalización"
        } else {
            this.label
        }
    }

    companion object {
        fun fromLabel(label: String): EstadoProtocolo {
            return entries.find {
                it.label.equals(label, ignoreCase = true) ||
                it.name.equals(label, ignoreCase = true) ||
                (label.contains("Borrador", ignoreCase = true) && it == BORRADOR) ||
                (label.contains("Observado", ignoreCase = true) && it == OBSERVADO) ||
                (label.contains("Formalizado", ignoreCase = true) && it == FORMALIZADO) ||
                (label.contains("Terreno", ignoreCase = true) && it == REGISTRADO_TERRENO) ||
                (label.contains("Pendiente", ignoreCase = true) && it == REGISTRADO_TERRENO)
            } ?: REGISTRADO_TERRENO
        }
    }
}
