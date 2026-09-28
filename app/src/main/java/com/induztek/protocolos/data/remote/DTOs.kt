// ============================================================================
// ARCHIVO : data/remote/DTOs.kt
// CAPA    : Datos remotos
// RESUMEN : DTO = Data Transfer Object: clases con la forma exacta del JSON que enviaría/recibiría el servidor.
//           Aquí los estados son texto (String), no enums, porque así viajan por internet.
// ============================================================================

package com.induztek.protocolos.data.remote

// Lo que se ENVÍA al hacer login.
data class LoginRequest(
    val email: String,
    val pass: String
)

// Lo que RESPONDE el servidor al hacer login (incluye un token de sesión).
data class LoginResponse(
    val token: String,
    val usuario: String,
    val email: String
)

// Equipo tal como viaja por la red.
data class EquipoDto(
    val id: String,
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    val ultimoEstado: String
)

// Prueba tal como viaja por la red.
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

// Medición tal como viaja por la red.
data class MedicionDto(
    val campo: String,
    val valor: String
)

// Protocolo tal como viaja por la red.
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
