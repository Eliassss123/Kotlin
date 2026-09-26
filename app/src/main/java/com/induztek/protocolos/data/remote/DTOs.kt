package com.induztek.protocolos.data.remote

data class LoginRequest(
    val email: String,
    val pass: String
)

data class LoginResponse(
    val token: String,
    val usuario: String,
    val email: String
)

data class EquipoDto(
    val id: String,
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    val ultimoEstado: String
)

data class PruebaDto(
    val id: String,
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    val mediciones: List<MedicionDto>,
    val estado: String,
    val observaciones: String
)

data class MedicionDto(
    val campo: String,
    val valor: String
)

data class ProtocoloDto(
    val id: String,
    val codigoProtocolo: String,
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: String,
    val tecnico: String,
    val observaciones: String,
    val mediciones: List<MedicionDto>
)
