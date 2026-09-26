package com.induztek.protocolos.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object ListaEquipos : Routes("lista_equipos")
    object DetalleEquipo : Routes("detalle_equipo/{codigo}") {
        fun createRoute(codigo: String) = "detalle_equipo/$codigo"
    }
    object NuevaPrueba : Routes("nueva_prueba")
    object ResumenProtocolo : Routes("resumen_protocolo")
    object ListaProtocolos : Routes("lista_protocolos")
    object DetalleProtocolo : Routes("detalle_protocolo/{id}") {
        fun createRoute(id: String) = "detalle_protocolo/$id"
    }
}
