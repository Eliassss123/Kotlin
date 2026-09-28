// ============================================================================
// ARCHIVO : ui/theme/Color.kt
// CAPA    : Tema visual
// RESUMEN : la paleta de colores de la app. Color(0xFF0F3460): 0x = hexadecimal, 'FF' = opacidad total, '0F3460' =
//           rojo/verde/azul
//           (igual que #0F3460 en CSS).
// ============================================================================

package com.induztek.protocolos.ui.theme

import androidx.compose.ui.graphics.Color

// Colores de marca: azul petróleo (principal) y naranja de seguridad (acento).
// Color Palette for Induztek Protocolos MVP
val AzulPetroleo = Color(0xFF0F3460)
val AzulPetroleoDark = Color(0xFF0A2240)
val AzulPetroleoLight = Color(0xFF164478)

val NaranjaSeguridad = Color(0xFFFFA000)

// Colores de los estados: verde = formalizado, ámbar = registrado, rojo = fuera de rango.
val EstadoFormalizadoVerde = Color(0xFF2E7D32)
val EstadoRegistradoAmbar = Color(0xFFF9A825)
val EstadoFueraDeRangoRojo = Color(0xFFC62828)

// Colores de fondo y texto.
val FondoApp = Color(0xFFF5F5F5)
val TextoPrincipal = Color(0xFF2E2E2E)
val TextoSecundario = Color(0xFF666666)
val TarjetaFondo = Color(0xFFFFFFFF)
val BordeFondo = Color(0xFFE0E0E0)
