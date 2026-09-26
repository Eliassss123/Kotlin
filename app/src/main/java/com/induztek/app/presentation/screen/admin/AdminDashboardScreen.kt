package com.induztek.app.presentation.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.induztek.app.domain.model.EstadoPrueba
import com.induztek.app.domain.model.Prueba
import com.induztek.app.presentation.screen.admin.viewmodel.AdminDashboardViewModel

/**
 * Panel de Control del Supervisor / Administrador de Induztek Ingeniería.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToDetalle: (pruebaId: Long) -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Supervisor", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Induztek Ingeniería SpA · Gestión y Formalización",
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ── Métricas / KPI Cards ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiCard(
                    title = "Terreno",
                    count = uiState.totalEnTerreno,
                    icon = Icons.Default.PendingActions,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Sync",
                    count = uiState.totalSincronizadas,
                    icon = Icons.Default.Sync,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Formal",
                    count = uiState.totalFormalizadas,
                    icon = Icons.Default.AssignmentTurnedIn,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Chips de Filtro por Estado ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = uiState.filtroEstado == null,
                    onClick = { viewModel.onFiltrarPorEstado(null) },
                    label = { Text("Todos (${uiState.pruebas.size})") }
                )
                FilterChip(
                    selected = uiState.filtroEstado == EstadoPrueba.REGISTRADO_TERRENO,
                    onClick = { viewModel.onFiltrarPorEstado(EstadoPrueba.REGISTRADO_TERRENO) },
                    label = { Text("Terreno") }
                )
                FilterChip(
                    selected = uiState.filtroEstado == EstadoPrueba.FORMALIZADO,
                    onClick = { viewModel.onFiltrarPorEstado(EstadoPrueba.FORMALIZADO) },
                    label = { Text("Formalizados") }
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Lista de Pruebas a gestionar ──────────────────────────────
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.pruebasFiltradas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No hay pruebas en esta categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.pruebasFiltradas,
                        key = { it.id }
                    ) { prueba ->
                        PruebaAdminCard(
                            prueba = prueba,
                            onVerDetalle = { onNavigateToDetalle(prueba.id) },
                            onFormalizar = { viewModel.formalizarPrueba(prueba.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(4.dp))
            Text(count.toString(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun PruebaAdminCard(
    prueba: Prueba,
    onVerDetalle: () -> Unit,
    onFormalizar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prueba #${prueba.id} · Equipo ID #${prueba.equipoId}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                EstadoBadgeSmall(estado = prueba.estado)
            }

            Text(
                text = "Técnico: ${prueba.tecnico} · Fecha: ${prueba.fecha} ${prueba.hora}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (prueba.mediciones.isNotEmpty()) {
                val medResumen = prueba.mediciones.entries.take(2).joinToString(" | ") { "${it.key}: ${it.value}" }
                Text(
                    text = "Mediciones: $medResumen",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVerDetalle) {
                    Text("Ver Protocolo")
                }

                if (prueba.estado != EstadoPrueba.FORMALIZADO) {
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onFormalizar,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Formalizar")
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadgeSmall(estado: EstadoPrueba) {
    val (label, containerColor, contentColor) = when (estado) {
        EstadoPrueba.REGISTRADO_TERRENO -> Triple("En Terreno", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        EstadoPrueba.PENDIENTE_SYNC -> Triple("Pendiente Sync", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        EstadoPrueba.SINCRONIZADO -> Triple("Sincronizado", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        EstadoPrueba.FORMALIZADO -> Triple("Formalizado", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.primary)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}
