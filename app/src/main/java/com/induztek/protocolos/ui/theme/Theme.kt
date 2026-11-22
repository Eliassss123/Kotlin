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

private val InduztekColorScheme = darkColorScheme(
    primary = InduztekBlue,
    onPrimary = Color.White,
    primaryContainer = ChipFondo,
    onPrimaryContainer = TextoPrincipal,
    secondary = NaranjaSeguridad,
    onSecondary = Color.Black,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = TarjetaFondo,
    onSurface = TextoPrincipal,
    surfaceVariant = ChipFondo,
    onSurfaceVariant = TextoSecundario,
    outline = BordeFondo,
    error = EstadoFueraDeRangoRojo,
    onError = Color.White
)

@Composable
fun InduztekTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = InduztekColorScheme
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
