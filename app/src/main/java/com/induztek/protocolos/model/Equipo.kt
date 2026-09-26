package com.induztek.protocolos.model

data class Equipo(
    val id: String,
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    val ultimoEstado: EstadoProtocolo
)
