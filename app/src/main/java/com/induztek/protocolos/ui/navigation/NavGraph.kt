// ============================================================================
// ARCHIVO : ui/navigation/NavGraph.kt
// CAPA    : Navegación
// RESUMEN : el MAPA de la app: dice qué pantalla se muestra en cada ruta y cómo se pasa de una a otra.
//           Las pantallas no navegan por sí mismas: avisan con lambdas (onLoginSuccess...) y AQUÍ se decide el
//             destino.
//
//           Flujo principal:
//           Login -> ListaEquipos -> DetalleEquipo
//                        |-> NuevaPrueba -> ResumenProtocolo -> ListaProtocolos -> DetalleProtocolo
//           Recuperar contraseña: Login -> OlvideContrasena -> VerificarCodigo -> NuevaContrasena -> Login
// ============================================================================

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
import com.induztek.protocolos.ui.login.NuevaContrasenaScreen
import com.induztek.protocolos.ui.login.OlvideContrasenaScreen
import com.induztek.protocolos.ui.login.VerificarCodigoScreen
import com.induztek.protocolos.ui.protocolos.DetalleProtocoloScreen
import com.induztek.protocolos.ui.protocolos.ListaProtocolosScreen
import com.induztek.protocolos.ui.pruebas.NuevaPruebaScreen
import com.induztek.protocolos.ui.pruebas.ResumenProtocoloScreen
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel

@Composable
// Recibe el controlador de navegación y los 4 ViewModels (se comparten entre pantallas).
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    equipoViewModel: EquipoViewModel,
    pruebaViewModel: PruebaViewModel,
    protocoloViewModel: ProtocoloViewModel
) {
    // NavHost = contenedor que muestra la pantalla de la ruta actual. 'startDestination' = pantalla inicial (Login).
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // 1. Login
        // composable(ruta) { ... } = 'cuando la ruta sea esta, dibuja esta pantalla'.
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                // Lambda: qué hacer cuando el login sale bien -> ir a la lista de equipos.
                onLoginSuccess = {
                    navController.navigate(Routes.ListaEquipos.route) {
                        // popUpTo(... inclusive = true) borra el Login del historial: al presionar 'atrás' NO se
                        //   vuelve al login.
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onOlvideContrasenaClick = {
                    navController.navigate(Routes.OlvideContrasena.route)
                }
            )
        }

        // 1.1 Olvidé mi contraseña
        composable(Routes.OlvideContrasena.route) {
            OlvideContrasenaScreen(
                viewModel = loginViewModel,
                onCodigoEnviado = {
                    navController.navigate(Routes.VerificarCodigo.route)
                },
                onNavigateBack = {
                    // popBackStack = volver a la pantalla anterior.
                    navController.popBackStack()
                }
            )
        }

        // 1.2 Verificar código
        composable(Routes.VerificarCodigo.route) {
            VerificarCodigoScreen(
                viewModel = loginViewModel,
                onCodigoVerificado = {
                    navController.navigate(Routes.NuevaContrasena.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 1.3 Nueva contraseña
        composable(Routes.NuevaContrasena.route) {
            NuevaContrasenaScreen(
                viewModel = loginViewModel,
                onContrasenaActualizada = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 2. Lista de Equipos
        composable(Routes.ListaEquipos.route) {
            ListaEquiposScreen(
                viewModel = equipoViewModel,
                // Recibe el código del equipo tocado y navega al detalle armando la ruta con createRoute.
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
        // Ruta CON PARÁMETRO: navArgument declara que 'codigo' es texto; luego se lee desde backStackEntry.arguments.
        composable(
            route = Routes.DetalleEquipo.route,
            arguments = listOf(navArgument("codigo") { type = NavType.StringType })
        ) { backStackEntry ->
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            DetalleEquipoScreen(
                codigoEquipo = codigo,
                viewModel = equipoViewModel,
                onNavigateBack = { navController.popBackStack() },
                // OJO: 'equipoCodigo' llega pero no se usa: la pantalla Nueva Prueba no preselecciona ese equipo
                //      (elige el primero de la lista).
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
                        // Al guardar, se limpia el historial hasta la lista de equipos: 'atrás' no vuelve al
                        //   formulario ya guardado.
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
        // Ruta con parámetro 'id' para abrir un protocolo concreto.
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
