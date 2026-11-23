/**
 * =====================================================================================
 * INDUZTEK PROTOCOLOS - NAVEGACIÓN PRINCIPAL (AppNavegacion.kt)
 * =====================================================================================
 * Este archivo define el grafo de navegación de Jetpack Compose (NavHost).
 * Conecta todas las pantallas de la aplicación móvil y gestiona el traspaso de ViewModels
 * y argumentos entre rutas (Login, Equipos, Visitas, Protocolos, Administración y Perfil).
 */
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
import com.induztek.protocolos.ui.login.RegistroScreen
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

/**
 * Composable principal que inicializa el controlador de navegación (NavController)
 * y declara cada una de las rutas accesibles en la aplicación.
 */
@Composable
fun AppNavegacion(
    loginViewModel: LoginViewModel,
    equipoViewModel: EquipoViewModel,
    pruebaViewModel: PruebaViewModel,
    protocoloViewModel: ProtocoloViewModel,
    visitaViewModel: VisitaViewModel,
    adminViewModel: AdminViewModel
) {
    // Controlador que maneja el historial de pantallas y transiciones
    val navController = rememberNavController()

    // Callback para manejar la selección de pestañas en la barra de navegación inferior (InduztekBottomBar)
    val onTabSelected: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Routes.ListaEquipos.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Estados globales del usuario autenticado actual
    val usuarioActual by loginViewModel.usuarioActivo.collectAsState()
    val profileError by loginViewModel.profileError.collectAsState()

    // Contenedor de navegación que define la pantalla inicial y el mapa de rutas
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        // ==========================================
        // 1. FLUJO DE AUTENTICACIÓN Y RECUPERACIÓN
        // ==========================================

        // Pantalla de Inicio de Sesión
        composable(route = Routes.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Refrescar repositorios locales al iniciar sesión y navegar a equipos
                    equipoViewModel.refresh()
                    protocoloViewModel.refresh()
                    visitaViewModel.refresh()
                    navController.navigate(Routes.ListaEquipos.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onOlvideContrasenaClick = {
                    navController.navigate(Routes.OlvideContrasena.route)
                },
                onRegistroClick = {
                    navController.navigate(Routes.Registro.route)
                }
            )
        }

        // Pantalla de Registro de Nueva Cuenta de Cliente
        composable(route = Routes.Registro.route) {
            RegistroScreen(
                viewModel = loginViewModel,
                onRegistered = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Recuperación de Contraseña (Paso 1: Ingreso de correo)
        composable(route = Routes.OlvideContrasena.route) {
            OlvideContrasenaScreen(
                viewModel = loginViewModel,
                onCodigoEnviado = {
                    navController.navigate(Routes.VerificarCodigo.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Verificación de Código de 6 dígitos (Paso 2)
        composable(route = Routes.VerificarCodigo.route) {
            VerificarCodigoScreen(
                viewModel = loginViewModel,
                onCodigoVerificado = {
                    navController.navigate(Routes.NuevaContrasena.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Ingreso de Nueva Contraseña (Paso 3)
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

        // ==========================================
        // 2. MÓDULO DE EQUIPOS (FT-03 / RT-02)
        // ==========================================

        // Pantalla Principal: Listado de Equipos Eléctricos en Terreno
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

        // Pantalla de Registro de Nuevo Equipo en Terreno (RT-02)
        composable(route = Routes.CrearEquipo.route) {
            CrearEquipoScreen(
                viewModel = equipoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEquipoCreado = { navController.popBackStack() }
            )
        }

        // Pantalla de Ficha Técnica e Historial del Equipo (FT-03)
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

        // ==========================================
        // 3. MÓDULO DE VISITAS TÉCNICAS (RT-01)
        // ==========================================

        // Pantalla Principal: Listado de Visitas Técnicas
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
                },
                onTabSelected = onTabSelected
            )
        }

        // Pantalla de Creación de Nueva Visita Técnica (RT-01)
        composable(route = Routes.NuevaVisita.route) {
            NuevaVisitaScreen(
                visitaViewModel = visitaViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onVisitaCreada = { visitaId ->
                    navController.navigate(Routes.DetalleVisita.createRoute(visitaId)) {
                        popUpTo(Routes.ListaVisitas.route)
                    }
                },
                onTabSelected = onTabSelected
            )
        }

        // Pantalla de Detalle de Visita, Protocolos Asociados y Cierre de Acta (RT-08)
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

        // ==========================================
        // 4. MÓDULO DE PROTOCOLOS Y PRUEBAS (RT-03)
        // ==========================================

        // Pantalla Principal: Historial General de Protocolos de Prueba
        composable(route = Routes.ListaProtocolos.route) {
            ListaProtocolosScreen(
                viewModel = protocoloViewModel,
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() },
                onProtocoloSelected = { id ->
                    navController.navigate(Routes.DetalleProtocolo.createRoute(id))
                },
                onTabSelected = onTabSelected
            )
        }

        // Pantalla de Detalle de Protocolo, Evidencia Fotográfica y Formalización (RN-16)
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

        // Pantalla de Captura y Edición de Mediciones por Prueba (RT-04)
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

        // Pantalla de Visualización de Acta Oficial de Entrega (RT-08)
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

        // ==========================================
        // 5. ADMINISTRACIÓN Y PERFIL DE USUARIO
        // ==========================================

        // Panel de Administración Técnica (Gestión de Personal e Instrumentos)
        composable(route = Routes.Admin.route) {
            AdminScreen(
                adminViewModel = adminViewModel,
                usuarioActual = usuarioActual,
                onNavigateBack = { navController.popBackStack() },
                onTabSelected = onTabSelected
            )
        }

        // Pantalla de Perfil de Usuario, Fotografía y Edición de Datos
        composable(route = Routes.Perfil.route) {
            PerfilScreen(
                usuario = usuarioActual,
                profileError = profileError,
                onVolver = { navController.popBackStack() },
                onActualizarFoto = { path ->
                    loginViewModel.actualizarFotoPerfil(path)
                },
                onActualizarBiografia = { bio ->
                    loginViewModel.actualizarBiografia(bio)
                },
                onActualizarPerfilCompleto = { nombre, correo, empresa, bio ->
                    loginViewModel.actualizarPerfilCompleto(nombre, correo, empresa, bio)
                },
                onCerrarSesion = {
                    loginViewModel.cerrarSesion()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = onTabSelected
            )
        }
    }
}
