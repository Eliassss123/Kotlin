// ============================================================================
// ARCHIVO : ui/theme/Color.kt
// CAPA    : Tema visual
// RESUMEN : Paleta de colores luminosa, moderna y clara para Induztek.
// ============================================================================

package com.induztek.protocolos.ui.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Azul corporativo profundo, oscuro y elegante
val AzulPetroleo = Color(0xFF0F365F)        // Azul petróleo oscuro, sobrio y elegante
val AzulPetroleoDark = Color(0xFF0A2542)    // Azul petróleo más oscuro para barra de estado
val AzulPetroleoLight = Color(0xFF1E5285)   // Azul intermedio para contrastes
val AzulSuaveFondo = Color(0xFFEBF3FA)      // Azul pastel suave para tarjetas o chips

// Naranja cálido (color del rayo eléctrico)
val NaranjaSeguridad = Color(0xFFFF9800)

// Colores de los estados
val EstadoFormalizadoVerde = Color(0xFF2E7D32)
val EstadoRegistradoAmbar = Color(0xFFF57C00)
val EstadoFueraDeRangoRojo = Color(0xFFD32F2F)

// Fondos claros, luminosos y limpios (Adiós al azul oscuro invasivo)
val FondoApp = Color(0xFFF8FAFC)            // Fondo blanco humo limpio (Slate 50)
val TarjetaFondo = Color(0xFFFFFFFF)        // Blanco puro para tarjetas
val BordeFondo = Color(0xFFE2E8F0)          // Bordes sutiles y elegantes

// Tipografía nítida con contraste garantizado
val TextoPrincipal = Color(0xFF0F172A)      // Texto casi negro súper nítido (Slate 900)
val TextoSecundario = Color(0xFF475569)     // Texto secundario gris oscuro legible (Slate 600)

/**
 * Colores estándar para OutlinedTextField en toda la aplicación.
 * Garantiza que el texto tipeado, las etiquetas y los bordes tengan máximo contraste
 * y nunca se muestren en blanco sobre fondo claro.
 */
@Composable
fun induztekTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextoPrincipal,
    unfocusedTextColor = TextoPrincipal,
    disabledTextColor = TextoSecundario,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedLabelColor = AzulPetroleo,
    unfocusedLabelColor = TextoSecundario,
    focusedBorderColor = AzulPetroleo,
    unfocusedBorderColor = Color(0xFF94A3B8),
    cursorColor = AzulPetroleo
)
