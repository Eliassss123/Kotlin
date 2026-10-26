











package com.induztek.protocolos.model

import java.util.UUID

data class Protocolo(
    val id: String = UUID.randomUUID().toString(),
    val codigoProtocolo: String = "PT-2026-0001",
    val visitaId: String = "",
    val ordenTrabajo: String = "OT-2026-084",
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: EstadoProtocolo = EstadoProtocolo.BORRADOR,
    val resultadoGlobal: ResultadoPrueba = ResultadoPrueba.APROBADO,
    val tecnico: String = "Juan Pérez (Técnico de Terreno)",
    val tecnicoCreador: String = "Juan Pérez",
    val descripcionMantenimiento: String = "",
    val instrumento: String = "Megger MIT525",
    val calibracionVigente: Boolean = true,
    val temperaturaAmbiente: String = "22°C",
    val humedad: String = "45%",
    val actaEntregadaCliente: Boolean = true,
    val formalizadoPor: String? = null,
    val fechaFormalizacion: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDIENTE,
    val observaciones: String = "",
    val fotos: List<String> = emptyList(),
    val pruebas: List<Prueba> = emptyList(),
    val mediciones: List<Medicion> = emptyList()
) {
    val esInmutable: Boolean
        get() = estado == EstadoProtocolo.FORMALIZADO

    fun getEstadoVisible(esCliente: Boolean): String {
        return estado.getLabelCliente(esCliente)
    }
}
