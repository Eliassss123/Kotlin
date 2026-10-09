// ============================================================================
// ARCHIVO : model/Equipo.kt
// CAPA    : Modelo (dominio)
// RESUMEN : Equipo eléctrico según Anexo 12 (FT-03 sec. 2) y RT-02/RT-07:
//           - TAG, tipo, marca, nº serie, potencia, año, tensiones, tap, conexión (datos de placa)
//           - Detección de duplicados en la misma instalación (RT-02)
//           - Indicador de tendencia/empeoramiento histórico (RT-07)
// ============================================================================

package com.induztek.protocolos.model

import java.util.UUID

data class Equipo(
    val id: String = UUID.randomUUID().toString(),
    // Código interno Induztek o TAG de equipo (p. ej. TR-500KVA-01)
    val codigo: String,
    // TAG asignado por el cliente (p. ej. TAG-TR-N1)
    val tagCliente: String = "",
    // Tipo: Transformador de poder, Relé de protección, Celda MT, Interruptor, Cable de poder
    val tipo: String,
    // Nombre de la empresa cliente (Planta Lampa, Minera San Pedro, etc.)
    val cliente: String = "",
    // Instalación específica dentro de la planta
    val ubicacion: String,
    // Datos de placa de características (FT-03 sec. 2)
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val potenciaTension: String = "",
    val anio: String = "2020",
    val tensiones: String = "23 kV / 380 V",
    val tap: String = "Tap 3 (Normal)",
    val conexion: String = "Dyn11",
    // Estado del último protocolo registrado
    val ultimoEstado: EstadoProtocolo = EstadoProtocolo.REGISTRADO_TERRENO,
    // RT-07: Indicador de si el equipo viene empeorando en su historial
    val empeorando: Boolean = false
) {
    val clasificacion: ClasificacionEquipo
        get() = TipoEquipo.fromNombre(tipo).clasificacion
}
