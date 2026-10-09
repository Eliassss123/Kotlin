// ============================================================================
// ARCHIVO : ui/navigation/Routes.kt
// CAPA    : Navegación
// RESUMEN : Mapa de rutas de navegación de la aplicación Induztek.
// ============================================================================

package com.induztek.protocolos.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object OlvideContrasena : Routes("olvide_contrasena")
    object VerificarCodigo : Routes("verificar_codigo")
    object NuevaContrasena : Routes("nueva_contrasena")

    object ListaEquipos : Routes("lista_equipos")
    object CrearEquipo : Routes("crear_equipo")
    object DetalleEquipo : Routes("detalle_equipo/{codigo}") {
        fun createRoute(codigo: String) = "detalle_equipo/$codigo"
    }

    object ListaVisitas : Routes("lista_visitas")
    object NuevaVisita : Routes("nueva_visita")
    object DetalleVisita : Routes("detalle_visita/{id}") {
        fun createRoute(id: String) = "detalle_visita/$id"
    }

    object CapturaPrueba : Routes("captura_prueba/{id}") {
        fun createRoute(id: String) = "captura_prueba/$id"
    }

    object ListaProtocolos : Routes("lista_protocolos")
    object DetalleProtocolo : Routes("detalle_protocolo/{id}") {
        fun createRoute(id: String) = "detalle_protocolo/$id"
    }

    object DetalleActa : Routes("detalle_acta/{id}") {
        fun createRoute(id: String) = "detalle_acta/$id"
    }

    object Admin : Routes("admin")
}
