// ============================================================================
// ARCHIVO : MainActivity.kt
// CAPA    : arranque de la app (punto de entrada de la interfaz)
// RESUMEN : es la ÚNICA Activity. Crea los 4 ViewModels y le entrega todo al NavGraph, que decide qué pantalla
//           mostrar.
//           Aquí NO hay diseño de pantalla: solo se "enciende" Compose con setContent { }.
// ============================================================================

package com.induztek.protocolos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.induztek.protocolos.ui.navigation.NavGraph
import com.induztek.protocolos.ui.theme.InduztekTheme
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel
import com.induztek.protocolos.viewmodel.PruebaViewModel

// ComponentActivity = Activity compatible con Compose.
class MainActivity : ComponentActivity() {

    // 'by viewModels { }' = pide a Android el ViewModel (lo crea una vez y lo conserva al rotar la pantalla).
    // Como LoginViewModel necesita un repositorio en su constructor, hay que darle una 'fábrica' (Factory) que sepa
    //   construirlo.
    // Las 4 fábricas de abajo son iguales: sacan los repositorios de InduztekApp y crean el ViewModel.
    private val loginViewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // 'as?' = convertir de forma segura (si falla da null). '?:' (operador Elvis) = si es null, prueba lo
                //   siguiente; al final error() detiene con un mensaje.
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                // Silencia una advertencia técnica: la Factory devuelve un tipo genérico T y Kotlin no puede
                //   comprobarlo.
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app.usuarioRepository) as T
            }
        }
    }

    // Igual que el anterior, pero para la lista/detalle de equipos (necesita 2 repositorios).
    private val equipoViewModel: EquipoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return EquipoViewModel(app.equipoRepository, app.pruebaRepository) as T
            }
        }
    }

    // ViewModel del formulario 'Nueva prueba' (necesita 3 repositorios).
    private val pruebaViewModel: PruebaViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return PruebaViewModel(app.equipoRepository, app.pruebaRepository, app.protocoloRepository) as T
            }
        }
    }

    // ViewModel de la lista/detalle de protocolos guardados.
    private val protocoloViewModel: ProtocoloViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as? InduztekApp
                    ?: (applicationContext as? InduztekApp)
                    ?: error("Application is not InduztekApp")
                @Suppress("UNCHECKED_CAST")
                return ProtocoloViewModel(app.protocoloRepository) as T
            }
        }
    }

    // onCreate: primer método que Android ejecuta al abrir la pantalla.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent { }: aquí empieza Jetpack Compose. Todo lo de adentro es interfaz escrita en Kotlin (no XML).
        setContent {
            // InduztekTheme: aplica colores y tipografías de la app (ui/theme). Es como una hoja de estilos global.
            InduztekTheme {
                // Surface: superficie de fondo que ocupa toda la pantalla y usa el color 'background' del tema.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // navController: el 'control remoto' para navegar entre pantallas. 'remember' lo conserva entre
                    //   redibujados.
                    val navController = rememberNavController()
                    // NavGraph: mapa de pantallas (ui/navigation/NavGraph.kt). Recibe los ViewModels para
                    //   compartirlos con las pantallas.
                    NavGraph(
                        navController = navController,
                        loginViewModel = loginViewModel,
                        equipoViewModel = equipoViewModel,
                        pruebaViewModel = pruebaViewModel,
                        protocoloViewModel = protocoloViewModel
                    )
                }
            }
        }
    }
}
