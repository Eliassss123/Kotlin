package com.induztek.app.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Paleta de colores Induztek ─────────────────────────────────────────────
// Azul industrial como color primario (apropiado para empresa de ingeniería eléctrica)
private val InduztekBlue   = Color(0xFF1A5276)
private val InduztekOrange = Color(0xFFE67E22)   // acento para estados de alerta
private val InduztekGray   = Color(0xFF566573)

private val LightColorScheme = lightColorScheme(
    primary          = InduztekBlue,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFD6EAF8),
    secondary        = InduztekOrange,
    onSecondary      = Color.White,
    tertiary         = InduztekGray
)

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF85C1E9),
    onPrimary        = Color(0xFF0D2137),
    primaryContainer = Color(0xFF1A4A6E),
    secondary        = InduztekOrange,
    tertiary         = Color(0xFF99A3A4)
)

/**
 * Tema Material Design 3 de la app Induztek.
 *
 * Soporta:
 * - Dynamic Color (Android 12+): usa los colores del fondo de pantalla del usuario.
 * - Dark/Light mode automático según configuración del sistema.
 * - Fallback a la paleta InduztekBlue en dispositivos < Android 12.
 */
@Composable
fun InduztekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,   // desactivar en producción si queremos branding fijo
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
