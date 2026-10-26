







package com.induztek.protocolos.navigation

sealed class Rutas(val ruta: String) {

    
    data object Login           : Rutas("login")
    data object OlvideContrasena: Rutas("olvide_contrasena")
    data object VerificarCodigo : Rutas("verificar_codigo")
    data object NuevaContrasena : Rutas("nueva_contrasena")

    
    data object ListaEquipos    : Rutas("lista_equipos")
    data object CrearEquipo     : Rutas("crear_equipo")
    data object DetalleEquipo   : Rutas("detalle_equipo/{codigo}") {
        fun crear(codigo: String) = "detalle_equipo/$codigo"
    }

    
    data object ListaVisitas    : Rutas("lista_visitas")
    data object NuevaVisita     : Rutas("nueva_visita")
    data object DetalleVisita   : Rutas("detalle_visita/{visitaId}") {
        fun crear(id: String) = "detalle_visita/$id"
    }

    
    data object ListaProtocolos : Rutas("lista_protocolos")
    data object DetalleProtocolo: Rutas("detalle_protocolo/{id}") {
        fun crear(id: String) = "detalle_protocolo/$id"
    }
    data object CapturaPrueba   : Rutas("captura_prueba/{pruebaId}") {
        fun crear(id: String) = "captura_prueba/$id"
    }

    
    data object DetalleActa     : Rutas("detalle_acta/{actaId}") {
        fun crear(id: String) = "detalle_acta/$id"
    }

    
    data object Admin           : Rutas("admin")

    
    data object Perfil          : Rutas("perfil")
}
