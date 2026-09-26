package com.induztek.app.presentation.screen.equipos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.induztek.app.domain.model.Equipo
import com.induztek.app.domain.model.TipoEquipo
import com.induztek.app.presentation.screen.equipos.viewmodel.ListaEquiposViewModel

/**
 * Catálogo Pro de Equipos e Instalaciones — Interfaz Moderna, Intuitiva y de Alta Rapidez.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEquiposScreen(
    onNavigateToNuevaPrueba: (equipoId: Long) -> Unit,
    onNavigateToHistorial: (equipoId: Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: ListaEquiposViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ElectricalServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                "INDUZTEK",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = Color(0xFF10B981), modifier = Modifier.size(6.dp)) {}
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Modo Terreno · 100% Offline",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Hero Banner Informativo de Faena ──────────────────────────
            HeroFaenaBanner(totalEquipos = uiState.equipos.size)

            // ── Buscador Rápido con botón de limpieza ──────────────────────
            SearchBarModern(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            // ── Selector de Secciones Temáticas ────────────────────────────
            CategorySectionCarousel(
                selectedCategory = uiState.categoriaSeleccionada,
                onCategorySelected = viewModel::onCategoriaSelected,
                totalTodos = uiState.equipos.size,
                totalTrf = uiState.totalTransformadores,
                totalRele = uiState.totalReles,
                totalCelda = uiState.totalCeldas,
                totalCable = uiState.totalCables,
                totalMalla = uiState.totalMallasTierra,
                totalGen = uiState.totalGeneradores
            )

            Spacer(Modifier.height(6.dp))

            // ── Lista de Tarjetas de Equipos ───────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                uiState.equiposFiltrados.isEmpty() -> {
                    EmptyStateView(query = uiState.searchQuery)
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.equiposFiltrados,
                            key = { it.id }
                        ) { equipo ->
                            ModernEquipoCard(
                                equipo = equipo,
                                onVerHistorial = { onNavigateToHistorial(equipo.id) },
                                onNuevaPrueba = { onNavigateToNuevaPrueba(equipo.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-componentes visuales modernos
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroFaenaBanner(totalEquipos: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Catálogo de Inspección Técnica",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$totalEquipos activos en subestaciones y plantas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("SQLite", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SearchBarModern(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar por código, cliente o tipo...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = MaterialTheme.colorScheme.outline)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun CategorySectionCarousel(
    selectedCategory: TipoEquipo?,
    onCategorySelected: (TipoEquipo?) -> Unit,
    totalTodos: Int,
    totalTrf: Int,
    totalRele: Int,
    totalCelda: Int,
    totalCable: Int,
    totalMalla: Int,
    totalGen: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ModernCategoryChip(
            title = "Todos",
            count = totalTodos,
            isSelected = selectedCategory == null,
            icon = Icons.Default.Tune,
            onClick = { onCategorySelected(null) }
        )
        ModernCategoryChip(
            title = "Transformadores",
            count = totalTrf,
            isSelected = selectedCategory == TipoEquipo.TRANSFORMADOR,
            icon = Icons.Default.Bolt,
            onClick = { onCategorySelected(TipoEquipo.TRANSFORMADOR) }
        )
        ModernCategoryChip(
            title = "Relés",
            count = totalRele,
            isSelected = selectedCategory == TipoEquipo.RELE,
            icon = Icons.Default.Security,
            onClick = { onCategorySelected(TipoEquipo.RELE) }
        )
        ModernCategoryChip(
            title = "Celdas MT",
            count = totalCelda,
            isSelected = selectedCategory == TipoEquipo.CELDA,
            icon = Icons.Default.DeveloperBoard,
            onClick = { onCategorySelected(TipoEquipo.CELDA) }
        )
        ModernCategoryChip(
            title = "Cables",
            count = totalCable,
            isSelected = selectedCategory == TipoEquipo.CABLE,
            icon = Icons.Default.Cable,
            onClick = { onCategorySelected(TipoEquipo.CABLE) }
        )
        ModernCategoryChip(
            title = "Mallas SPT",
            count = totalMalla,
            isSelected = selectedCategory == TipoEquipo.MALLA_TIERRA,
            icon = Icons.Default.Foundation,
            onClick = { onCategorySelected(TipoEquipo.MALLA_TIERRA) }
        )
        ModernCategoryChip(
            title = "Generadores",
            count = totalGen,
            isSelected = selectedCategory == TipoEquipo.GENERADOR,
            icon = Icons.Default.Power,
            onClick = { onCategorySelected(TipoEquipo.GENERADOR) }
        )
    }
}

@Composable
private fun ModernCategoryChip(
    title: String,
    count: Int,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            )
        },
        label = {
            Text(
                "$title ($count)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun ModernEquipoCard(
    equipo: Equipo,
    onVerHistorial: () -> Unit,
    onNuevaPrueba: () -> Unit
) {
    val visual = when (equipo.tipo) {
        TipoEquipo.TRANSFORMADOR -> VisualStyle(
            icono = Icons.Default.Bolt,
            gradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
            bgLight = Color(0xFFE0F2FE),
            textColor = Color(0xFF0369A1),
            tag = "Transformador AT/MT"
        )
        TipoEquipo.RELE -> VisualStyle(
            icono = Icons.Default.Security,
            gradient = listOf(Color(0xFF059669), Color(0xFF047857)),
            bgLight = Color(0xFFD1FAE5),
            textColor = Color(0xFF047857),
            tag = "Relé de Protección"
        )
        TipoEquipo.CELDA -> VisualStyle(
            icono = Icons.Default.DeveloperBoard,
            gradient = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
            bgLight = Color(0xFFEDE9FE),
            textColor = Color(0xFF6D28D9),
            tag = "Celda MT Maniobra"
        )
        TipoEquipo.CABLE -> VisualStyle(
            icono = Icons.Default.Cable,
            gradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
            bgLight = Color(0xFFFEF3C7),
            textColor = Color(0xFFB45309),
            tag = "Cable Alimentador"
        )
        TipoEquipo.MALLA_TIERRA -> VisualStyle(
            icono = Icons.Default.Foundation,
            gradient = listOf(Color(0xFFDC2626), Color(0xFFB91C1C)),
            bgLight = Color(0xFFFEE2E2),
            textColor = Color(0xFFB91C1C),
            tag = "Puesta a Tierra (SEC)"
        )
        TipoEquipo.GENERADOR -> VisualStyle(
            icono = Icons.Default.Power,
            gradient = listOf(Color(0xFF475569), Color(0xFF334155)),
            bgLight = Color(0xFFF1F5F9),
            textColor = Color(0xFF334155),
            tag = "Grupo Electrógeno"
        )
        TipoEquipo.OTRO -> VisualStyle(
            icono = Icons.Default.ElectricalServices,
            gradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
            bgLight = Color(0xFFDBEAFE),
            textColor = Color(0xFF1D4ED8),
            tag = "Equipo Eléctrico"
        )
    }

    ElevatedCard(
        onClick = onVerHistorial,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Ícono con degradado elegante
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            brush = Brush.linearGradient(visual.gradient),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icono,
                        contentDescription = visual.tag,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = equipo.codigo,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = visual.bgLight
                        ) {
                            Text(
                                text = visual.tag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = visual.textColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = equipo.instalacion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (equipo.descripcion.isNotBlank()) {
                Text(
                    text = equipo.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

            // ── Botones de Acción Rápida en 1 Toque ────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onVerHistorial,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Historial (2024-26)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                FilledTonalButton(
                    onClick = onNuevaPrueba,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nueva Prueba", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (query.isBlank()) "No hay equipos registrados en esta sección." else "Sin resultados para \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class VisualStyle(
    val icono: ImageVector,
    val gradient: List<Color>,
    val bgLight: Color,
    val textColor: Color,
    val tag: String
)
