// ============================================================================
// ARCHIVO : ui/theme/Type.kt
// CAPA    : Tema visual
// RESUMEN : estilos de texto (tipografía). Es como definir h1, h2, p en una hoja de estilos.
//           'sp' = unidad de tamaño de letra (respeta la configuración de fuente del usuario).
// ============================================================================

package com.induztek.protocolos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sobrescribe solo algunos estilos del tema Material (titleLarge, bodyLarge...). Los demás usan valores por defecto.
val Typography = Typography(
    // Títulos grandes: negrita, 22sp.
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = TextoPrincipal
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = TextoPrincipal
    ),
    // Texto normal.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = TextoPrincipal
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = TextoSecundario
    ),
    // Etiquetas de botones.
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
)
