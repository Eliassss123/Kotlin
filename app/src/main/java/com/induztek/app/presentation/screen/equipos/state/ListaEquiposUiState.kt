package com.induztek.app.presentation.screen.equipos.state

import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.TipoEquipo

/**
 * Estado inmutable de la pantalla ListaEquipos con categorías ampliadas.
 */
data class ListaEquiposUiState(
    val equipos: List<Equipo>                  = emptyList(),
    val equiposFiltrados: List<Equipo>         = emptyList(),
    val categoriaSeleccionada: TipoEquipo?     = null, // null = "Todos"
    val searchQuery: String                    = "",
    val totalTransformadores: Int              = 0,
    val totalReles: Int                        = 0,
    val totalCeldas: Int                       = 0,
    val totalCables: Int                       = 0,
    val totalMallasTierra: Int                 = 0,
    val totalGeneradores: Int                  = 0,
    val isLoading: Boolean                     = true,
    val errorMessage: String?                  = null
)
