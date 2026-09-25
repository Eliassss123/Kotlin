package com.induztek.app.presentation.screen.equipos

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
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
 * Pantalla de inicio del Técnico con Catálogo por Secciones (Transformadores, Relés, Celdas, Cables, Mallas, Generadores).
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
                    Column {
                        Text("Induztek Terreno", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Catálogo de Equipos e Instalaciones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Campo de búsqueda ──────────────────────────────────────────
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Buscar por código, cliente o descripción...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            // ── Barra de Secciones / Categorías de Equipos ─────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opción: Todos
                FilterChip(
                    selected = uiState.categoriaSeleccionada == null,
                    onClick = { viewModel.onCategoriaSelected(null) },
                    label = { Text("⚡ Todos (${uiState.equipos.size})") }
                )

                // Sección 1: Transformadores
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.TRANSFORMADOR,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.TRANSFORMADOR) },
                    leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Transformadores (${uiState.totalTransformadores})") }
                )

                // Sección 2: Relés de Protección
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.RELE,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.RELE) },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Relés (${uiState.totalReles})") }
                )

                // Sección 3: Celdas de Media Tensión
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.CELDA,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.CELDA) },
                    leadingIcon = { Icon(Icons.Default.DeveloperBoard, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Celdas MT (${uiState.totalCeldas})") }
                )

                // Sección 4: Cables Eléctricos
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.CABLE,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.CABLE) },
                    leadingIcon = { Icon(Icons.Default.Cable, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Cables (${uiState.totalCables})") }
                )

                // Sección 5: Mallas a Tierra (SEC RIC 06)
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.MALLA_TIERRA,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.MALLA_TIERRA) },
                    leadingIcon = { Icon(Icons.Default.Foundation, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Mallas SPT (${uiState.totalMallasTierra})") }
                )

                // Sección 6: Generadores
                FilterChip(
                    selected = uiState.categoriaSeleccionada == TipoEquipo.GENERADOR,
                    onClick = { viewModel.onCategoriaSelected(TipoEquipo.GENERADOR) },
                    leadingIcon = { Icon(Icons.Default.Power, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Generadores (${uiState.totalGeneradores})") }
                )
            }

            Spacer(Modifier.height(4.dp))

            // ── Estados de la lista ────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                uiState.equiposFiltrados.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No se encontraron equipos en esta sección.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = uiState.equiposFiltrados,
                            key = { it.id }
                        ) { equipo ->
                            EquipoCard(
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

/**
 * Tarjeta visual para un equipo con ícono temático según tipo.
 */
@Composable
private fun EquipoCard(
    equipo: Equipo,
    onVerHistorial: () -> Unit,
    onNuevaPrueba: () -> Unit
) {
    val visual = when (equipo.tipo) {
        TipoEquipo.TRANSFORMADOR -> VisualEquipo(
            icono = Icons.Default.Bolt,
            colorContenedor = Color(0xFFD6EAF8),
            colorIcono = Color(0xFF1A5276),
            nombreSeccion = "Transformador"
        )
        TipoEquipo.RELE -> VisualEquipo(
            icono = Icons.Default.Security,
            colorContenedor = Color(0xFFD5F5E3),
            colorIcono = Color(0xFF1E8449),
            nombreSeccion = "Relé de Protección"
        )
        TipoEquipo.CELDA -> VisualEquipo(
            icono = Icons.Default.DeveloperBoard,
            colorContenedor = Color(0xFFE8DAEF),
            colorIcono = Color(0xFF6C3483),
            nombreSeccion = "Celda MT"
        )
        TipoEquipo.CABLE -> VisualEquipo(
            icono = Icons.Default.Cable,
            colorContenedor = Color(0xFFFCF3CF),
            colorIcono = Color(0xFFB7950B),
            nombreSeccion = "Cable Eléctrico"
        )
        TipoEquipo.MALLA_TIERRA -> VisualEquipo(
            icono = Icons.Default.Foundation,
            colorContenedor = Color(0xFFFADBD8),
            colorIcono = Color(0xFF922B21),
            nombreSeccion = "Malla Puesta a Tierra"
        )
        TipoEquipo.GENERADOR -> VisualEquipo(
            icono = Icons.Default.Power,
            colorContenedor = Color(0xFFE5E7E9),
            colorIcono = Color(0xFF2E4053),
            nombreSeccion = "Grupo Electrógeno"
        )
        TipoEquipo.OTRO -> VisualEquipo(
            icono = Icons.Default.ElectricalServices,
            colorContenedor = Color(0xFFEAECEE),
            colorIcono = Color(0xFF2C3E50),
            nombreSeccion = "Equipo Especial"
        )
    }

    Card(
        onClick = onVerHistorial,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícono circular con color de la sección
                Surface(
                    shape = CircleShape,
                    color = visual.colorContenedor,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = visual.icono,
                            contentDescription = visual.nombreSeccion,
                            tint = visual.colorIcono,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = equipo.codigo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = visual.colorContenedor
                        ) {
                            Text(
                                text = visual.nombreSeccion,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = visual.colorIcono,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = equipo.instalacion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (equipo.descripcion.isNotBlank()) {
                Text(
                    text = equipo.descripcion,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(modifier = Modifier.padding(top = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVerHistorial) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.padding(start = 4.dp))
                    Text("Ver Historial")
                }

                FilledTonalButton(onClick = onNuevaPrueba) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.padding(start = 4.dp))
                    Text("Nueva Prueba")
                }
            }
        }
    }
}

private data class VisualEquipo(
    val icono: ImageVector,
    val colorContenedor: Color,
    val colorIcono: Color,
    val nombreSeccion: String
)
