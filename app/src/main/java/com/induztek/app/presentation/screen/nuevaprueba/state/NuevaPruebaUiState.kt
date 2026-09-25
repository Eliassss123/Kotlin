package com.induztek.app.presentation.screen.nuevaprueba.state

import com.induztek.app.domain.model.TipoEquipo

/**
 * Estado inmutable de la pantalla "Nueva Prueba".
 */
data class NuevaPruebaUiState(
    // ── Equipo seleccionado ───────────────────────────────────────────────
    val equipoId: Long? = null,
    val nombreEquipo: String = "",
    val tipoEquipo: TipoEquipo = TipoEquipo.TRANSFORMADOR,

    // ── Formulario ────────────────────────────────────────────────────────
    val tecnico: String = "",
    val fecha: String = "",          // "YYYY-MM-DD"
    val hora: String = "",           // "HH:mm"
    val observaciones: String = "",
    val mediciones: Map<String, String> = emptyMap(),

    // ── Foto ──────────────────────────────────────────────────────────────
    val fotoUriPath: String? = null,

    // ── UI feedback ───────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,

    // ── Permisos ──────────────────────────────────────────────────────────
    val cameraPermissionGranted: Boolean = false
)
