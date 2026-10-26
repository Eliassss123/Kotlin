






package com.induztek.protocolos.data.remote


data class LoginRequest(
    val email: String,
    val pass: String
)


data class LoginResponse(
    val token: String,
    val usuario: String,
    val email: String,
    val perfilCodigo: String = "P-01",
    val empresaCliente: String? = null
)


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


data class MedicionDto(
    val campo: String,
    val valor: String
)


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
