package com.induztek.protocolos.data.remote

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val token: String,
    val usuario: String,
    val email: String,
    val perfilCodigo: String,
    val empresaCliente: String? = null,
    val roleCode: String = "",
    val activo: Boolean = true
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val company: String? = null,
    val roleCode: String? = null,
    val termsAccepted: Boolean
)

data class RecoveryRequest(val email: String)

data class VerifyResetCodeRequest(val email: String, val code: String)

data class ResetPasswordRequest(val email: String, val code: String, val newPassword: String)

data class MessageDto(val message: String)

data class RoleRequest(val roleCode: String)

data class UserResponseDto(
    val id: String,
    val email: String,
    val name: String,
    val roleCode: String,
    val perfilCodigo: String,
    val company: String?,
    val termsAccepted: Boolean,
    val termsAcceptedAt: String?,
    val active: Boolean,
    val photoUrl: String?,
    val biography: String?
)

data class UserProfileUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    val company: String? = null,
    val photoUrl: String? = null,
    val biography: String? = null
)

data class ActiveRequest(val active: Boolean)

data class RoleDto(val id: String, val code: String, val name: String, val description: String?)

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
    val anio: String = "2020",
    val tensiones: String = "",
    val tap: String = "",
    val conexion: String = "",
    val ultimoEstado: String
)

data class EquipmentRequest(
    val codigo: String,
    val tagCliente: String,
    val tipo: String,
    val cliente: String,
    val ubicacion: String,
    val marca: String,
    val modelo: String,
    val numeroSerie: String,
    val potenciaTension: String,
    val anio: String,
    val tensiones: String,
    val tap: String,
    val conexion: String,
    val ultimoEstado: String
)

data class DuplicateResponse(
    val duplicate: Boolean,
    val equipment: DuplicateEquipmentDto?
)

data class DuplicateEquipmentDto(
    val id: String,
    val codigo: String,
    val tagCliente: String?,
    val numeroSerie: String?
)

data class MedicionDto(
    val campo: String,
    @SerializedName(value = "valorNumerico", alternate = ["valor"])
    val valorNumerico: String? = null,
    @SerializedName(value = "unidad", alternate = ["unit"])
    val unidad: String = "",
    val id: String? = null,
    val momento: String? = null,
    val instrumentoInfo: String? = null
)

data class TestDto(
    val id: String,
    val protocoloId: String = "",
    val ordenTrabajo: String = "",
    val equipoCodigo: String,
    val tipoPrueba: String,
    val ubicacion: String = "",
    val fechaHora: String = "",
    val estadoPrueba: String = "REALIZADA",
    val motivoNoRealizada: String = "",
    val resultado: String = "APROBADO",
    val instrumentoId: String = "",
    val instrumento: String = "",
    val instrumentoSerie: String = "",
    val instrumentoCalibracion: String = "",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "",
    val humedad: String = "",
    val mediciones: List<MedicionDto> = emptyList(),
    val observaciones: String = "",
    val variacionHistoricaPorcentaje: Double? = null,
    val superaVariacion25: Boolean = false
)

data class ProtocolDto(
    val id: String,
    val codigoProtocolo: String,
    val visitaId: String = "",
    val ordenTrabajo: String = "",
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: String,
    val resultadoGlobal: String = "APROBADO",
    val tecnico: String = "",
    val tecnicoCreador: String = "",
    val descripcionMantenimiento: String = "",
    val instrumento: String = "",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "",
    val humedad: String = "",
    val actaEntregadaCliente: Boolean = false,
    val formalizadoPor: String? = null,
    val fechaFormalizacion: String? = null,
    val syncStatus: String = "PENDIENTE",
    val observaciones: String = "",
    val pruebas: List<TestDto> = emptyList(),
    val mediciones: List<MedicionDto> = emptyList(),
    val fotos: List<String> = emptyList()
)

data class VisitDto(
    val id: String,
    val ordenTrabajo: String,
    val fecha: String,
    val horaInicio: String,
    val cliente: String,
    val instalacion: String,
    val modalidad: String,
    val ingenieroACargo: String,
    val tecnicos: String,
    val temperaturaAmbiente: String,
    val humedad: String,
    val cerrada: Boolean,
    val syncStatus: String
)

data class ActaDto(
    val id: String,
    val visitaId: String,
    val ordenTrabajo: String,
    val cliente: String,
    val instalacion: String,
    val fechaEmision: String,
    val tecnicoEntrega: String,
    val recibidoPor: String,
    val marcaPendienteFormalizacion: Boolean,
    val resumenProtocolos: String,
    val syncStatus: String
)

data class InstrumentDto(
    val id: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val numeroSerie: String,
    val vencimientoCalibracion: String,
    val activo: Boolean
)

data class ImageUploadResponse(val filename: String, val url: String)
