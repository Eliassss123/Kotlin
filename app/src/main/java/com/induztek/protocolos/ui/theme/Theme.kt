// ============================================================================
// ARCHIVO : ui/theme/Theme.kt
// CAPA    : Tema visual
// RESUMEN : une colores y tipografía en un tema (InduztekTheme) que envuelve TODA la app (ver MainActivity).
//           Los componentes toman de aquí sus colores por defecto: MaterialTheme.colorScheme.primary, etc.
// ============================================================================

package com.induztek.protocolos.ui.theme

import android.app.Activity
import android.content.ContextWrapper
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

// Paleta para modo claro. 'onPrimary' = color del texto que va SOBRE el color primary.
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

// Paleta para modo oscuro.
private val DarkColorScheme = darkColorScheme(
    primary = AzulPetroleoLight,
    onPrimary = Color.White,
    primaryContainer = AzulPetroleoDark,
    secondary = NaranjaSeguridad,
    background = AzulPetroleoDark,
    surface = AzulPetroleo
)

// @Composable = función que dibuja o configura interfaz.
@Composable
fun InduztekTheme(
    // Por defecto sigue el modo claro/oscuro del teléfono.
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 'content' = las pantallas que van DENTRO del tema (como el hijo de un componente).
    content: @Composable () -> Unit
) {
    // if como expresión: elige una paleta u otra.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    // isInEditMode = está en la vista previa de Android Studio; ahí no se toca la ventana.
    if (!view.isInEditMode) {
        // SideEffect = código que se ejecuta después de dibujar y toca cosas fuera de Compose. Aquí: pinta la barra
        //   de estado (la de arriba con la hora) del color primario.
        SideEffect {
            val context = view.context
            val activity = context as? Activity
                ?: (context as? ContextWrapper)?.baseContext as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    // Aplica el esquema de colores y la tipografía a todo lo que esté dentro de 'content'.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
