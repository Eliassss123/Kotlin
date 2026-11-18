






package com.induztek.protocolos.model

enum class SyncStatus(val label: String) {
    PENDIENTE("Pendiente de Sincronización"),
    ENVIADO("Sincronizado"),
    ERROR("Error de Sincronización");

    companion object {
        fun fromLabel(label: String): SyncStatus {
            return entries.find {
                it.label.equals(label, ignoreCase = true) || it.name.equals(label, ignoreCase = true)
            } ?: PENDIENTE
        }
    }
}
