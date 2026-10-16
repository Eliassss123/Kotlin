// ============================================================================
// ARCHIVO : model/Visita.kt
// CAPA    : Modelo (dominio)
// RESUMEN : Visita técnica en terreno según RT-01, Aclaración 4 y 7:
//           - OT, fecha, hora inicio, cliente, instalación
//           - Modalidad: SOLO_PRUEBAS o PRUEBAS_Y_MANTENIMIENTO
//           - Ingeniero a cargo OBLIGATORIO (aunque no asista a terreno)
//           - Técnicos, temperatura ambiente, humedad, sync_status y estado de cierre
// ============================================================================

package com.induztek.protocolos.model

import java.util.UUID

data class Visita(
    val id: String = UUID.randomUUID().toString(),
    val ordenTrabajo: String,
    val fecha: String,
    val horaInicio: String,
    val cliente: String,
    val instalacion: String,
    val modalidad: ModalidadVisita = ModalidadVisita.SOLO_PRUEBAS,
    val ingenieroACargo: String, // OBLIGATORIO (Aclar. 4 y 7)
    val tecnicos: String,
    val temperaturaAmbiente: String = "20°C",
    val humedad: String = "45%",
    val cerrada: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDIENTE
)
