package com.induztek.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.induztek.app.domain.model.RolUsuario
import com.induztek.app.presentation.screen.admin.AdminDashboardScreen
import com.induztek.app.presentation.screen.detalle.DetallePruebaScreen
import com.induztek.app.presentation.screen.equipos.ListaEquiposScreen
import com.induztek.app.presentation.screen.historial.HistorialEquipoScreen
import com.induztek.app.presentation.screen.login.LoginScreen
import com.induztek.app.presentation.screen.nuevaprueba.NuevaPruebaScreen

/**
 * Grafo de navegación centralizado con soporte de Roles (Técnico / Admin)
 * y flujo completo de terreno.
 */
@Composable
fun InduztekNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AppDestination.Login.route
    ) {

        // ── PANTALLA 0: Login Corporativo con RBAC ────────────────────────
        composable(route = AppDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = { rol ->
                    when (rol) {
                        RolUsuario.TECNICO -> {
                            navController.navigate(AppDestination.ListaEquipos.route) {
                                popUpTo(AppDestination.Login.route) { inclusive = true }
                            }
                        }
                        RolUsuario.ADMIN -> {
                            navController.navigate(AppDestination.AdminDashboard.route) {
                                popUpTo(AppDestination.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ── PANTALLA 1: Catálogo de Equipos (Técnico) ─────────────────────
        composable(route = AppDestination.ListaEquipos.route) {
            ListaEquiposScreen(
                onNavigateToNuevaPrueba = { equipoId ->
                    navController.navigate(AppDestination.NuevaPrueba.withArgs(equipoId))
                },
                onNavigateToHistorial = { equipoId ->
                    navController.navigate(AppDestination.HistorialEquipo.withArgs(equipoId))
                },
                onLogout = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── PANTALLA 2: Panel Supervisor / Admin (Formalización) ──────────
        composable(route = AppDestination.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToDetalle = { pruebaId ->
                    navController.navigate(AppDestination.DetallePrueba.withArgs(pruebaId))
                }
            )
        }

        // ── PANTALLA 3: Historial Comparativo de Mediciones ───────────────
        composable(
            route = AppDestination.HistorialEquipo.route,
            arguments = listOf(
                navArgument(AppDestination.HistorialEquipo.ARG_EQUIPO_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            HistorialEquipoScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNuevaPrueba = { equipoId ->
                    navController.navigate(AppDestination.NuevaPrueba.withArgs(equipoId))
                },
                onNavigateToDetalle = { pruebaId ->
                    navController.navigate(AppDestination.DetallePrueba.withArgs(pruebaId))
                }
            )
        }

        // ── PANTALLA 4: Formulario Nueva Prueba en Terreno ────────────────
        composable(
            route = AppDestination.NuevaPrueba.route,
            arguments = listOf(
                navArgument(AppDestination.NuevaPrueba.ARG_EQUIPO_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            NuevaPruebaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetalle = { pruebaId ->
                    navController.navigate(AppDestination.DetallePrueba.withArgs(pruebaId)) {
                        popUpTo(AppDestination.ListaEquipos.route)
                    }
                }
            )
        }

        // ── PANTALLA 5: Detalle / Certificado Pre-Protocolo ───────────────
        composable(
            route = AppDestination.DetallePrueba.route,
            arguments = listOf(
                navArgument(AppDestination.DetallePrueba.ARG_PRUEBA_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            DetallePruebaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
