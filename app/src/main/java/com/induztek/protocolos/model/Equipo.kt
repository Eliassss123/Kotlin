// ============================================================================
// ARCHIVO : model/Equipo.kt
// CAPA    : Modelo (dominio)
// RESUMEN : un equipo eléctrico (transformador, relé, celda...). Es la forma que usa la app internamente.
//           Ojo: 'val' = solo lectura (no se puede reasignar).
// ============================================================================

package com.induztek.protocolos.model

// data class = ficha de datos. Para 'modificar' un equipo se usa equipo.copy(ultimoEstado = ...), que crea una copia
//   nueva.
data class Equipo(
    val id: String,
    // Código legible del equipo (p. ej. TR-500KVA-01). Se usa para buscar.
    val codigo: String,
    val tipo: String,
    val ubicacion: String,
    // Estado del último protocolo. Su tipo es un enum (EstadoProtocolo), no un texto suelto.
    val ultimoEstado: EstadoProtocolo
)
