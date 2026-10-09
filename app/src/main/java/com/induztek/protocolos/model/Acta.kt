// ============================================================================
// ARCHIVO : model/Acta.kt
// CAPA    : Modelo (dominio)
// RESUMEN : Acta de entrega de visita según RT-08, Aclaración 2, 4 y 7:
//           - Una por visita, agrupa todos los protocolos del equipo visitado
//           - Pruebas no realizadas con su motivo obligatorio
//           - Marca "Pendiente de formalización"
//           - Registro del nombre de quien la recibe en terreno
// ============================================================================

package com.induztek.protocolos.model

import java.util.UUID

data class Acta(
    val id: String = UUID.randomUUID().toString(),
    val visitaId: String,
    val ordenTrabajo: String,
    val cliente: String,
    val instalacion: String,
    val fechaEmision: String,
    val tecnicoEntrega: String,
    val recibidoPor: String, // Obligatorio registrar nombre de quien la recibe
    val marcaPendienteFormalizacion: Boolean = true,
    val resumenProtocolos: String = "",
    val syncStatus: SyncStatus = SyncStatus.PENDIENTE
)
