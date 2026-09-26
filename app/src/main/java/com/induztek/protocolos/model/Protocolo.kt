package com.induztek.protocolos.model

data class Protocolo(
    val id: String,
    val codigoProtocolo: String,
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: EstadoProtocolo,
    val tecnico: String,
    val observaciones: String,
    val mediciones: List<Medicion>
)
