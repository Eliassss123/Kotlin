package com.induztek.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.induztek.app.presentation.navigation.InduztekNavGraph
import com.induztek.app.presentation.theme.InduztekTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la app (single-activity architecture).
 *
 * Responsabilidades:
 *   1. Inicializar el sistema de navegación (NavController).
 *   2. Aplicar el tema Material Design 3 (InduztekTheme).
 *   3. Delegar toda la UI al NavGraph (composables).
 *
 * @AndroidEntryPoint habilita inyección de dependencias Hilt en esta Activity.
 * enableEdgeToEdge() permite que la app se extienda bajo la barra de sistema
 * (Android 15+ la exige por defecto).
 *
 * Lo que NO hace esta Activity: lógica de negocio, acceso a datos, ni estado UI.
 * Todo eso vive en los ViewModels.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            InduztekTheme {
                val navController = rememberNavController()
                InduztekNavGraph(navController = navController)
            }
        }
    }
}
