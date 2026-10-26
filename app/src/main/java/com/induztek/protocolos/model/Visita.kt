









package com.induztek.protocolos.model

import java.util.UUID

data class Visita(
    val id: String = UUID.randomUUID().toString(),
    val ordenTrabajo: String,
    val fecha: String,
    val horaInicio: String,
    val cliente: String,
    val instalacion: String,
    val modalidad: ModalidadVisita = ModalidadVisita.SOLO_PRUEBAS,
    val ingenieroACargo: String, 
    val tecnicos: String,
    val temperaturaAmbiente: String = "20°C",
    val humedad: String = "45%",
    val cerrada: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDIENTE
)
