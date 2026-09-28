// ============================================================================
// ARCHIVO : ui/components/InduztekTopBar.kt
// CAPA    : View - componente reutilizable
// RESUMEN : la barra azul superior (título y flecha "volver"). Todas las pantallas internas la reutilizan.
// ============================================================================

package com.induztek.protocolos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.induztek.protocolos.ui.theme.AzulPetroleo

// @OptIn: acepto usar una API de Material 3 que Google marcó como 'experimental'.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
// Parámetros con valor por defecto: canNavigateBack = false (sin flecha) y onNavigateBack = {} (no hace nada).
fun InduztekTopBar(
    titulo: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    // TopAppBar = barra superior de Material.
    TopAppBar(
        // Las llaves { } reciben otro Composable (el contenido del título).
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        // Zona de la izquierda: solo muestra la flecha si canNavigateBack es true.
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            }
        },
        // Color de fondo de la barra.
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AzulPetroleo
        )
    )
}
