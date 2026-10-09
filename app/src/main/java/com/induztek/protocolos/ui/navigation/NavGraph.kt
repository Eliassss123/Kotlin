package com.induztek.protocolos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.induztek.protocolos.ui.protocolos.CapturaPruebaScreen
import com.induztek.protocolos.ui.protocolos.DetalleProtocoloScreen
import com.induztek.protocolos.ui.protocolos.ListaProtocolosScreen
import com.induztek.protocolos.ui.pruebas.NuevaPruebaScreen
import com.induztek.protocolos.ui.pruebas.ResumenProtocoloScreen
import com.induztek.protocolos.ui.visitas.DetalleVisitaScreen
import com.induztek.protocolos.ui.visitas.ListaVisitasScreen
import com.induztek.protocolos.ui.visitas.NuevaVisitaScreen
import com.induztek.protocolos.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    equipoViewModel: EquipoViewModel,
    pruebaViewModel: PruebaViewModel,
    protocoloViewModel: ProtocoloViewModel,
    visitaViewModel: VisitaViewModel,
    adminViewModel: AdminViewModel
) {
    val usuarioActual by loginViewModel.usuarioActivo.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // 1. Login
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.ListaEquipos.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onOlvideContrasenaClick = {
                    navController.navigate(Routes.OlvideContrasena.route)
                }
            )
        }

        // 1.1 Recuperación
        composable(Routes.OlvideContrasena.route) {
            OlvideContrasenaScreen(
                viewModel = loginViewModel,
                onCodigoEnviado = { navController.navigate(Routes.VerificarCodigo.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.VerificarCodigo.route) {
            VerificarCodigoScreen(
                viewModel = loginViewModel,
                onCodigoVerificado = { navController.navigate(Routes.NuevaContrasena.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NuevaContrasena.route) {
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

        // 2. Lista de Equipos (Centro operativo)
        composable(Routes.ListaEquipos.route) {
            ListaEquiposScreen(
                viewModel = equipoViewModel,
                loginViewModel = loginViewModel,
                onEquipoSelected = { id ->
                    navController.navigate(Routes.DetalleEquipo.createRoute(id))
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
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2.1 Crear Equipo nuevo en terreno (RT-02)
        composable(Routes.CrearEquipo.route) {
            CrearEquipoScreen(
                viewModel = equipoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEquipoCreado = {
                    navController.popBackStack()
                }
            )
        }

        // 2.2 Detalle de Equipo + Historial con alertas (RT-06 / RT-07)
        composable(
            route = Routes.DetalleEquipo.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetalleEquipoScreen(
                equipoId = id,
                viewModel = equipoViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onIniciarVisitaClick = {
                    navController.navigate(Routes.NuevaVisita.route)
                }
            )
        }

        // 3. Visitas Técnicas (RT-01, RT-03, RT-08)
        composable(Routes.ListaVisitas.route) {
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

        composable(Routes.NuevaVisita.route) {
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

        // 4. Protocolos
        composable(Routes.ListaProtocolos.route) {
            ListaProtocolosScreen(
                viewModel = protocoloViewModel,
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProtocoloSelected = { id ->
                    navController.navigate(Routes.DetalleProtocolo.createRoute(id))
                }
            )
        }

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

        // 4.1 Captura de Prueba (RT-04, RT-05, RT-06, RN-06..RN-09)
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

        // 5. Acta de Entrega (RT-08)
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

        // 6. Panel de Administración Técnica (RT-11, RF-22)
        composable(Routes.Admin.route) {
            AdminScreen(
                adminViewModel = adminViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
