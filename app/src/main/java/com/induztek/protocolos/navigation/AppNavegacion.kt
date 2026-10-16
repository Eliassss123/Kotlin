// ============================================================================
// ARCHIVO : navigation/AppNavegacion.kt
// CAPA    : Navegación
// RESUMEN : Punto central de navegación de la aplicación Induztek.
//           Declara TODAS las pantallas como composables, cada una con su URL (ruta)
//           y llama al constructor (función composable) de cada pantalla.
//           Sigue el patrón del profesor (AppNavegacion / NavHost).
// ============================================================================

package com.induztek.protocolos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.induztek.protocolos.ui.actas.DetalleActaScreen
import com.induztek.protocolos.ui.admin.AdminScreen
import com.induztek.protocolos.ui.equipos.CrearEquipoScreen
import com.induztek.protocolos.ui.equipos.DetalleEquipoScreen
import com.induztek.protocolos.ui.equipos.ListaEquiposScreen
import com.induztek.protocolos.ui.login.LoginScreen
import com.induztek.protocolos.ui.login.NuevaContrasenaScreen
import com.induztek.protocolos.ui.login.OlvideContrasenaScreen
import com.induztek.protocolos.ui.login.VerificarCodigoScreen
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.perfil.PerfilScreen
import com.induztek.protocolos.ui.protocolos.CapturaPruebaScreen
import com.induztek.protocolos.ui.protocolos.DetalleProtocoloScreen
import com.induztek.protocolos.ui.protocolos.ListaProtocolosScreen
import com.induztek.protocolos.ui.visitas.DetalleVisitaScreen
import com.induztek.protocolos.ui.visitas.ListaVisitasScreen
import com.induztek.protocolos.ui.visitas.NuevaVisitaScreen
import com.induztek.protocolos.viewmodel.AdminViewModel
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel
import com.induztek.protocolos.viewmodel.VisitaViewModel

// ─────────────────────────────────────────────────────────────────────────────
// AppNavegacion: composable raíz de la navegación.
// Recibe todos los ViewModels desde MainActivity y los distribuye a cada pantalla.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AppNavegacion(
    loginViewModel: LoginViewModel,
    equipoViewModel: EquipoViewModel,
    pruebaViewModel: PruebaViewModel,
    protocoloViewModel: ProtocoloViewModel,
    visitaViewModel: VisitaViewModel,
    adminViewModel: AdminViewModel
) {
    // NavController: controla la pila de pantallas (back stack)
    val navController = rememberNavController()

    // Estado del usuario activo (se usa en varias pantallas)
    val usuarioActual by loginViewModel.usuarioActivo.collectAsState()

    // NavHost: declara todas las rutas de la aplicación.
    // Cada composable(ruta) recibe la URL de la pantalla y llama a su constructor.
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route   // Pantalla inicial
    ) {

        // ── 1. LOGIN ─────────────────────────────────────────────────────────
        // URL: "login"
        composable(route = Routes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Al iniciar sesión, va a Lista de Equipos y limpia el back stack
                    navController.navigate(Routes.ListaEquipos.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onOlvideContrasenaClick = {
                    navController.navigate(Routes.OlvideContrasena.route)
                }
            )
        }

        // ── 1.1 OLVIDÉ MI CONTRASEÑA ─────────────────────────────────────────
        // URL: "olvide_contrasena"
        composable(route = Routes.OlvideContrasena.route) {
            OlvideContrasenaScreen(
                viewModel = loginViewModel,
                onCodigoEnviado = {
                    navController.navigate(Routes.VerificarCodigo.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 1.2 VERIFICAR CÓDIGO ─────────────────────────────────────────────
        // URL: "verificar_codigo"
        composable(route = Routes.VerificarCodigo.route) {
            VerificarCodigoScreen(
                viewModel = loginViewModel,
                onCodigoVerificado = {
                    navController.navigate(Routes.NuevaContrasena.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 1.3 NUEVA CONTRASEÑA ─────────────────────────────────────────────
        // URL: "nueva_contrasena"
        composable(route = Routes.NuevaContrasena.route) {
            NuevaContrasenaScreen(
                viewModel = loginViewModel,
                onContrasenaActualizada = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 2. LISTA DE EQUIPOS (pantalla principal / centro operativo) ───────
        // URL: "lista_equipos"
        composable(route = Routes.ListaEquipos.route) {
            ListaEquiposScreen(
                viewModel = equipoViewModel,
                loginViewModel = loginViewModel,
                onEquipoSelected = { codigo ->
                    navController.navigate(Routes.DetalleEquipo.createRoute(codigo))
                },
                onCrearEquipoClick = {
                    navController.navigate(Routes.CrearEquipo.route)
                },
                onVerVisitasClick = {
                    navController.navigate(Routes.ListaVisitas.route)
                },
                onVerProtocolosClick = {
                    navController.navigate(Routes.ListaProtocolos.route)
                },
                onAdminClick = {
                    navController.navigate(Routes.Admin.route)
                },
                onVerPerfilClick = {
                    navController.navigate(Routes.Perfil.route)
                },
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── 2.1 CREAR EQUIPO ─────────────────────────────────────────────────
        // URL: "crear_equipo"
        composable(route = Routes.CrearEquipo.route) {
            CrearEquipoScreen(
                viewModel = equipoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEquipoCreado = { navController.popBackStack() }
            )
        }

        // ── 2.2 DETALLE DE EQUIPO ────────────────────────────────────────────
        // URL: "detalle_equipo/{codigo}" — recibe el código del equipo como argumento
        composable(
            route = Routes.DetalleEquipo.route,
            arguments = listOf(navArgument("codigo") { type = NavType.StringType })
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            DetalleEquipoScreen(
                equipoId = codigo,
                viewModel = equipoViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onIniciarVisitaClick = {
                    navController.navigate(Routes.NuevaVisita.route)
                }
            )
        }

        // ── 3. LISTA DE VISITAS ──────────────────────────────────────────────
        // URL: "lista_visitas"
        composable(route = Routes.ListaVisitas.route) {
            ListaVisitasScreen(
                visitaViewModel = visitaViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onNuevaVisitaClick = {
                    navController.navigate(Routes.NuevaVisita.route)
                },
                onVisitaClick = { visitaId ->
                    navController.navigate(Routes.DetalleVisita.createRoute(visitaId))
                }
            )
        }

        // ── 3.1 NUEVA VISITA ─────────────────────────────────────────────────
        // URL: "nueva_visita"
        composable(route = Routes.NuevaVisita.route) {
            NuevaVisitaScreen(
                visitaViewModel = visitaViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onVisitaCreada = { visitaId ->
                    navController.navigate(Routes.DetalleVisita.createRoute(visitaId)) {
                        popUpTo(Routes.ListaVisitas.route)
                    }
                }
            )
        }

        // ── 3.2 DETALLE DE VISITA ────────────────────────────────────────────
        // URL: "detalle_visita/{visitaId}" — recibe el ID de la visita
        composable(
            route = Routes.DetalleVisita.route,
            arguments = listOf(navArgument("visitaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val visitaId = backStackEntry.arguments?.getString("visitaId") ?: ""
            DetalleVisitaScreen(
                visitaId = visitaId,
                viewModel = visitaViewModel,
                equipoViewModel = equipoViewModel,
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProtocoloSelected = { protocoloId ->
                    navController.navigate(Routes.DetalleProtocolo.createRoute(protocoloId))
                },
                onActaGenerada = { actaId ->
                    navController.navigate(Routes.DetalleActa.createRoute(actaId))
                }
            )
        }

        // ── 4. LISTA DE PROTOCOLOS ───────────────────────────────────────────
        // URL: "lista_protocolos"
        composable(route = Routes.ListaProtocolos.route) {
            ListaProtocolosScreen(
                viewModel = protocoloViewModel,
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProtocoloSelected = { id ->
                    navController.navigate(Routes.DetalleProtocolo.createRoute(id))
                }
            )
        }

        // ── 4.1 DETALLE DE PROTOCOLO ─────────────────────────────────────────
        // URL: "detalle_protocolo/{id}" — recibe el ID del protocolo
        composable(
            route = Routes.DetalleProtocolo.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetalleProtocoloScreen(
                idProtocolo = id,
                viewModel = protocoloViewModel,
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditarPrueba = { prueba ->
                    navController.navigate(Routes.CapturaPrueba.createRoute(prueba.id))
                }
            )
        }

        // ── 4.2 CAPTURA DE PRUEBA ────────────────────────────────────────────
        // URL: "captura_prueba/{pruebaId}" — recibe el ID de la prueba
        composable(
            route = Routes.CapturaPrueba.route,
            arguments = listOf(navArgument("pruebaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pruebaId = backStackEntry.arguments?.getString("pruebaId") ?: ""
            CapturaPruebaScreen(
                pruebaId = pruebaId,
                protocoloViewModel = protocoloViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 5. DETALLE DE ACTA ───────────────────────────────────────────────
        // URL: "detalle_acta/{actaId}" — recibe el ID del acta
        composable(
            route = Routes.DetalleActa.route,
            arguments = listOf(navArgument("actaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actaId = backStackEntry.arguments?.getString("actaId") ?: ""
            DetalleActaScreen(
                actaId = actaId,
                visitaViewModel = visitaViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 6. ADMINISTRACIÓN TÉCNICA ────────────────────────────────────────
        // URL: "admin"
        composable(route = Routes.Admin.route) {
            AdminScreen(
                adminViewModel = adminViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 7. PERFIL DEL USUARIO ────────────────────────────────────────────
        // URL: "perfil"
        composable(route = Routes.Perfil.route) {
            PerfilScreen(
                usuario = usuarioActual,
                onVolver = { navController.popBackStack() },
                onActualizarFoto = { path ->
                    loginViewModel.actualizarFotoPerfil(path)
                },
                onActualizarBiografia = { bio ->
                    loginViewModel.actualizarBiografia(bio)
                },
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
