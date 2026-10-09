// ============================================================================
// ARCHIVO : data/remote/DTOs.kt
// CAPA    : Datos remotos
// RESUMEN : DTO = Data Transfer Object: clases con la forma exacta del JSON que enviaría/recibiría el servidor.
//           Alineado con los requerimientos de Anexos 1, 2 y 3.
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
    val email: String,
    val perfilCodigo: String = "P-01",
    val empresaCliente: String? = null
)

// Equipo tal como viaja por la red.
data class EquipoDto(
    val id: String,
    val codigo: String,
    val tagCliente: String = "",
    val tipo: String,
    val cliente: String = "",
    val ubicacion: String,
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val potenciaTension: String = "",
    val ultimoEstado: String
)

// Prueba tal como viaja por la red.
data class PruebaDto(
    val id: String,
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String,
    val fechaHora: String,
    val instrumento: String = "Megger MIT525",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
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
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: String,
    val tecnico: String,
    val instrumento: String = "Megger MIT525",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val actaEntregadaCliente: Boolean = true,
    val observaciones: String,
    val mediciones: List<MedicionDto>
)
