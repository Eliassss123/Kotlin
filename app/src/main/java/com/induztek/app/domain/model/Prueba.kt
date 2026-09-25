package com.induztek.app.domain.model

/**
 * Modelo de dominio puro para una Prueba técnica en terreno.
 *
 * Estado del ciclo de vida de la prueba:
 * ┌─────────────────────┐
 * │ REGISTRADO_TERRENO  │ ← creada offline en el dispositivo
 * └──────────┬──────────┘
 *            │ sync exitoso con backend
 * ┌──────────▼──────────┐
 * │    SINCRONIZADO     │ ← el backend confirmó recepción
 * └──────────┬──────────┘
 *            │ técnico formaliza protocolo
 * ┌──────────▼──────────┐
 * │    FORMALIZADO      │ ← protocolo oficial emitido
 * └─────────────────────┘
 *
 * También existe PENDIENTE_SYNC (transición interna) que indica que hay
 * cambios locales aún no enviados al backend (offline-first).
 */
data class Prueba(
    val id: Long = 0,
    val equipoId: Long,
    val fecha: String,           // ISO-8601: "2026-09-23"
    val hora: String,            // "HH:mm"
    val tecnico: String,
    val mediciones: Map<String, String>, // ej. {"resistencia": "2.3 Ω", "tension": "13.2 kV"}
    val observaciones: String = "",
    val fotoUriPath: String? = null,     // ruta absoluta interna: /data/.../files/images/foto.jpg
    val estado: EstadoPrueba = EstadoPrueba.REGISTRADO_TERRENO,
    val sincronizadoAt: String? = null   // timestamp ISO-8601 de la última sync exitosa
)

enum class EstadoPrueba {
    REGISTRADO_TERRENO,
    PENDIENTE_SYNC,      // cambios locales sin enviar
    SINCRONIZADO,
    FORMALIZADO
}
