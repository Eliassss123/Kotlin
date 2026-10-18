// ============================================================================
// ARCHIVO : ui/theme/Theme.kt
// CAPA    : Tema visual
// RESUMEN : Tema en tonos oscuros (Dark Mode) para Induztek, garantizando alto contraste
//           y legibilidad óptima para el usuario.
// ============================================================================

package com.induztek.protocolos.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta en tonos oscuros de alto contraste
private val DarkColorScheme = darkColorScheme(
    primary = AzulPetroleo,
    onPrimary = Color.White,
    primaryContainer = AzulSuaveFondo,
    onPrimaryContainer = Color.White,
    secondary = NaranjaSeguridad,
    onSecondary = Color.Black,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = TarjetaFondo,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextoSecundario,
    outline = BordeFondo
)

@Composable
fun InduztekTheme(
    // Activamos por defecto el tema oscuro en tonos oscuros como lo solicitó el usuario
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            val activity = context as? Activity
                ?: (context as? ContextWrapper)?.baseContext as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = FondoApp.toArgb()
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
