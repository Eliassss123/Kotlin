package com.induztek.app.presentation.navigation

/**
 * Rutas de navegación de la app Induztek.
 */
sealed class AppDestination(val route: String) {

    /** Pantalla de inicio de sesión */
    data object Login : AppDestination("login")

    /** Lista de equipos (Técnico) */
    data object ListaEquipos : AppDestination("lista_equipos")

    /** Panel de control y formalización (Supervisor / Admin) */
    data object AdminDashboard : AppDestination("admin_dashboard")

    /** Formulario nueva prueba en terreno. Recibe el equipoId. */
    data object NuevaPrueba : AppDestination("nueva_prueba/{equipoId}") {
        const val ARG_EQUIPO_ID = "equipoId"
        fun withArgs(equipoId: Long) = "nueva_prueba/$equipoId"
    }

    /** Detalle y comprobante digital de la prueba registrada */
    data object DetallePrueba : AppDestination("detalle_prueba/{pruebaId}") {
        const val ARG_PRUEBA_ID = "pruebaId"
        fun withArgs(pruebaId: Long) = "detalle_prueba/$pruebaId"
    }

    /** Historial comparativo de un equipo */
    data object HistorialEquipo : AppDestination("historial_equipo/{equipoId}") {
        const val ARG_EQUIPO_ID = "equipoId"
        fun withArgs(equipoId: Long) = "historial_equipo/$equipoId"
    }
}
