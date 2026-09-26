package com.induztek.protocolos.model

data class Prueba(
    val id: String,
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    val mediciones: List<Medicion>,
    val estado: EstadoProtocolo,
    val observaciones: String
)
