// ============================================================================
// ARCHIVO : model/Protocolo.kt
// CAPA    : Modelo (dominio)
// RESUMEN : el documento final que se guarda tras una prueba (con código, técnico, estado y mediciones).
// ============================================================================

package com.induztek.protocolos.model

// Un Protocolo es el 'informe' de una prueba hecha a un equipo.
data class Protocolo(
    val id: String,
    // Código visible del protocolo (p. ej. IND-PROT-TR01).
    val codigoProtocolo: String,
    // Enlace al equipo por su código (no hay clave foránea real, es solo el mismo texto).
    val equipoCodigo: String,
    val tipoEquipo: String,
    val fechaHora: String,
    val estado: EstadoProtocolo,
    val tecnico: String,
    val observaciones: String,
    // List<Medicion> = lista de mediciones. Room no guarda listas directamente: ver data/local/ProtocoloEntity.kt (se
    //   guardan como JSON).
    val mediciones: List<Medicion>
)
