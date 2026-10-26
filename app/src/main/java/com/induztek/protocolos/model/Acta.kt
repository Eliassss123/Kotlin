









package com.induztek.protocolos.model

import java.util.UUID

data class Acta(
    val id: String = UUID.randomUUID().toString(),
    val visitaId: String,
    val ordenTrabajo: String,
    val cliente: String,
    val instalacion: String,
    val fechaEmision: String,
    val tecnicoEntrega: String,
    val recibidoPor: String, 
    val marcaPendienteFormalizacion: Boolean = true,
    val resumenProtocolos: String = "",
    val syncStatus: SyncStatus = SyncStatus.PENDIENTE
)
