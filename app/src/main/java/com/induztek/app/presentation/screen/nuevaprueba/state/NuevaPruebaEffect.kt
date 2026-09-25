package com.induztek.app.presentation.screen.nuevaprueba.state

/**
 * Efectos laterales de la pantalla "Nueva Prueba".
 *
 * Un Effect es un evento puntual y NO repetible (ej. navegar, mostrar Snackbar).
 * Se implementa con SharedFlow (replay=0) en el ViewModel para que la UI
 * lo consuma una sola vez aunque recomponga.
 *
 * Diferencia con UiState:
 *   UiState = condición persistente ("el formulario tiene errores")
 *   Effect   = evento puntual ("navegar a detalle ahora mismo")
 */
sealed interface NuevaPruebaEffect {
    /** Prueba guardada → navegar a la pantalla de detalle con el id generado. */
    data class NavigateToDetalle(val pruebaId: Long) : NuevaPruebaEffect

    /** Mostrar un Snackbar con el mensaje dado. */
    data class ShowSnackbar(val message: String) : NuevaPruebaEffect
}
