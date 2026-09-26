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

// ── Paleta de Alta Ingeniería y Energía (Induztek Pro) ──────────────────────
// Colores premium inspirados en software de ingeniería eléctrica industrial:
private val BrandBluePrimary       = Color(0xFF0F4C81) // Classic Classic Industrial Blue
private val BrandBlueLight         = Color(0xFFE3F2FD)
private val BrandCyanAccent        = Color(0xFF0284C7) // Energy Cyan
private val BrandAmberGold         = Color(0xFFD97706) // High Voltage Amber
private val BrandEmeraldGreen      = Color(0xFF059669) // Safe / Certified Grounding Green
private val BrandSurfaceLight      = Color(0xFFF8FAFC) // Clean Slate Light
private val BrandCardLight         = Color(0xFFFFFFFF)

// Dark Palette
private val BrandDarkBackground    = Color(0xFF0B132B) // Deep Midnight
private val BrandDarkSurface       = Color(0xFF1C2541)
private val BrandDarkCard          = Color(0xFF243256)
private val BrandDarkCyan          = Color(0xFF38BDF8)

private val LightColorScheme = lightColorScheme(
    primary              = BrandBluePrimary,
    onPrimary            = Color.White,
    primaryContainer     = Color(0xFFD0E4FF),
    onPrimaryContainer   = Color(0xFF001D36),

    secondary            = BrandCyanAccent,
    onSecondary          = Color.White,
    secondaryContainer   = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),

    tertiary             = BrandAmberGold,
    onTertiary           = Color.White,
    tertiaryContainer    = Color(0xFFFEF3C7),
    onTertiaryContainer  = Color(0xFF92400E),

    background           = BrandSurfaceLight,
    onBackground         = Color(0xFF0F172A),
    surface              = BrandCardLight,
    onSurface            = Color(0xFF0F172A),
    surfaceVariant       = Color(0xFFF1F5F9),
    onSurfaceVariant     = Color(0xFF475569),
    outline              = Color(0xFFCBD5E1),
    outlineVariant       = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary              = BrandDarkCyan,
    onPrimary            = Color(0xFF003355),
    primaryContainer     = Color(0xFF0A406B),
    onPrimaryContainer   = Color(0xFFD0E4FF),

    secondary            = Color(0xFF38BDF8),
    onSecondary          = Color(0xFF082F49),
    secondaryContainer   = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),

    tertiary             = Color(0xFFFBBF24),
    onTertiary           = Color(0xFF451A03),
    tertiaryContainer    = Color(0xFF78350F),
    onTertiaryContainer  = Color(0xFFFEF3C7),

    background           = BrandDarkBackground,
    onBackground         = Color(0xFFF8FAFC),
    surface              = BrandDarkSurface,
    onSurface            = Color(0xFFF8FAFC),
    surfaceVariant       = BrandDarkCard,
    onSurfaceVariant     = Color(0xFF94A3B8),
    outline              = Color(0xFF475569),
    outlineVariant       = Color(0xFF334155)
)

/**
 * Tema Visual Profesional Induztek con estética moderna de ingeniería.
 */
@Composable
fun InduztekTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desactivado para mantener la identidad visual corporativa de Induztek
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
