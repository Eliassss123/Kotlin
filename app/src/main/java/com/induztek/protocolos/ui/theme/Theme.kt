package com.induztek.protocolos.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AzulPetroleo,
    onPrimary = Color.White,
    primaryContainer = AzulPetroleoLight,
    onPrimaryContainer = Color.White,
    secondary = NaranjaSeguridad,
    onSecondary = Color.Black,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = TarjetaFondo,
    onSurface = TextoPrincipal
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulPetroleoLight,
    onPrimary = Color.White,
    primaryContainer = AzulPetroleoDark,
    secondary = NaranjaSeguridad,
    background = AzulPetroleoDark,
    surface = AzulPetroleo
)

@Composable
fun InduztekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
