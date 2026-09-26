package com.induztek.protocolos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.induztek.protocolos.ui.equipos.DetalleEquipoScreen
import com.induztek.protocolos.ui.equipos.ListaEquiposScreen
import com.induztek.protocolos.ui.login.LoginScreen
import com.induztek.protocolos.ui.protocolos.DetalleProtocoloScreen
import com.induztek.protocolos.ui.protocolos.ListaProtocolosScreen
import com.induztek.protocolos.ui.pruebas.NuevaPruebaScreen
import com.induztek.protocolos.ui.pruebas.ResumenProtocoloScreen
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    equipoViewModel: EquipoViewModel,
    pruebaViewModel: PruebaViewModel,
    protocoloViewModel: ProtocoloViewModel
) {
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
                }
            )
        }

        // 2. Lista de Equipos
        composable(Routes.ListaEquipos.route) {
            ListaEquiposScreen(
                viewModel = equipoViewModel,
                onEquipoSelected = { codigo ->
                    navController.navigate(Routes.DetalleEquipo.createRoute(codigo))
                },
                onNuevaPruebaClick = {
                    navController.navigate(Routes.NuevaPrueba.route)
                },
                onVerProtocolosClick = {
                    navController.navigate(Routes.ListaProtocolos.route)
                }
            )
        }

        // 3. Detalle de Equipo + Historial
        composable(
            route = Routes.DetalleEquipo.route,
            arguments = listOf(navArgument("codigo") { type = NavType.StringType })
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            DetalleEquipoScreen(
                codigoEquipo = codigo,
                viewModel = equipoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNuevaPruebaClick = { equipoCodigo ->
                    navController.navigate(Routes.NuevaPrueba.route)
                }
            )
        }

        // 4. Nueva Prueba
        composable(Routes.NuevaPrueba.route) {
            NuevaPruebaScreen(
                viewModel = pruebaViewModel,
                onNavigateBack = { navController.popBackStack() },
                onIrAResumen = {
                    navController.navigate(Routes.ResumenProtocolo.route)
                }
            )
        }

        // 5. Resumen / Confirmación
        composable(Routes.ResumenProtocolo.route) {
            ResumenProtocoloScreen(
                viewModel = pruebaViewModel,
                onNavigateBack = { navController.popBackStack() },
                onGuardarExito = {
                    navController.navigate(Routes.ListaProtocolos.route) {
                        popUpTo(Routes.ListaEquipos.route)
                    }
                }
            )
        }

        // 6. Lista de Protocolos
        composable(Routes.ListaProtocolos.route) {
            ListaProtocolosScreen(
                viewModel = protocoloViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProtocoloSelected = { id ->
                    navController.navigate(Routes.DetalleProtocolo.createRoute(id))
                }
            )
        }

        // 7. Detalle de Protocolo (Solo Lectura)
        composable(
            route = Routes.DetalleProtocolo.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetalleProtocoloScreen(
                idProtocolo = id,
                viewModel = protocoloViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
