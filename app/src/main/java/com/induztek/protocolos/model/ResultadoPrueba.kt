








package com.induztek.protocolos.model

enum class ResultadoPrueba(val label: String, val severidad: Int) {
    APROBADO("Aprobado", 1),
    OBSERVADO("Observado", 2),
    RECHAZADO("Rechazado", 3),
    NO_REALIZADA("No Realizada", 0);

    companion object {
        fun fromEstado(estado: EstadoProtocolo): ResultadoPrueba {
            return when (estado) {
                EstadoProtocolo.FORMALIZADO -> APROBADO
                EstadoProtocolo.REGISTRADO_TERRENO -> OBSERVADO
                EstadoProtocolo.OBSERVADO -> RECHAZADO
                EstadoProtocolo.BORRADOR -> APROBADO
            }
        }

        fun fromLabel(label: String): ResultadoPrueba {
            return entries.find {
                it.label.equals(label, ignoreCase = true) ||
                it.name.equals(label, ignoreCase = true) ||
                (label.contains("Aprobado", ignoreCase = true) && it == APROBADO) ||
                (label.contains("Observado", ignoreCase = true) && it == OBSERVADO) ||
                (label.contains("Rechazado", ignoreCase = true) && it == RECHAZADO) ||
                (label.contains("No Realizada", ignoreCase = true) && it == NO_REALIZADA)
            } ?: APROBADO
        }

        
        fun peorResultado(resultados: List<ResultadoPrueba>): ResultadoPrueba {
            val validos = resultados.filter { it != NO_REALIZADA }
            if (validos.isEmpty()) return NO_REALIZADA
            return validos.maxByOrNull { it.severidad } ?: APROBADO
        }
    }
}

enum class EstadoPrueba(val label: String) {
    REALIZADA("Realizada"),
    NO_REALIZADA("No Realizada");

    companion object {
        fun fromLabel(label: String): EstadoPrueba {
            return entries.find {
                it.label.equals(label, ignoreCase = true) || it.name.equals(label, ignoreCase = true)
            } ?: REALIZADA
        }
    }
}
