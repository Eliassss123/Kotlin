// ============================================================================
// ARCHIVO : ui/navigation/Routes.kt
// CAPA    : Navegación
// RESUMEN : la lista de "direcciones" de cada pantalla (como las URLs de una web). Centralizarlas evita errores de
//           tipeo.
// ============================================================================

package com.induztek.protocolos.ui.navigation

// sealed class = lista cerrada de rutas posibles. Cada 'object' es una pantalla y guarda su texto de ruta.
sealed class Routes(val route: String) {
    object Login : Routes("login")
    object OlvideContrasena : Routes("olvide_contrasena")
    object VerificarCodigo : Routes("verificar_codigo")
    object NuevaContrasena : Routes("nueva_contrasena")
    object ListaEquipos : Routes("lista_equipos")
    // '{codigo}' es un parámetro dentro de la ruta (como /equipo/:codigo en una web).
    object DetalleEquipo : Routes("detalle_equipo/{codigo}") {
        // Arma la ruta real reemplazando el parámetro. '$codigo' inserta el valor dentro del texto (string template).
        fun createRoute(codigo: String) = "detalle_equipo/$codigo"
    }
    object NuevaPrueba : Routes("nueva_prueba")
    object ResumenProtocolo : Routes("resumen_protocolo")
    object ListaProtocolos : Routes("lista_protocolos")
    object DetalleProtocolo : Routes("detalle_protocolo/{id}") {
        fun createRoute(id: String) = "detalle_protocolo/$id"
    }
}
