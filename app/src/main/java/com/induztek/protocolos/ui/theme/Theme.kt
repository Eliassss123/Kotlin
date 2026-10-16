// ============================================================================
// ARCHIVO : ui/theme/Theme.kt
// CAPA    : Tema visual
// RESUMEN : Tema luminoso y claro para Induztek. Evita el fondo azul invasivo
//           y garantiza excelente contraste y legibilidad en todos los dispositivos.
// ============================================================================

package com.induztek.protocolos.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta clara, luminosa y limpia
private val LightColorScheme = lightColorScheme(
    primary = AzulPetroleo,
    onPrimary = Color.White,
    primaryContainer = AzulSuaveFondo,
    onPrimaryContainer = AzulPetroleoDark,
    secondary = NaranjaSeguridad,
    onSecondary = Color.White,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = TarjetaFondo,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextoSecundario,
    outline = BordeFondo
)

@Composable
fun InduztekTheme(
    // Por defecto forzamos el tema claro y luminoso para asegurar máxima legibilidad en terreno
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            val activity = context as? Activity
                ?: (context as? ContextWrapper)?.baseContext as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = AzulPetroleoDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
