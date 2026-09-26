package com.induztek.app.presentation.screen.detalle

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.induztek.app.domain.model.EstadoPrueba
import java.io.File

/**
 * Pantalla de Comprobante / Pre-Protocolo Digital en Terreno con Certificación Normativa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePruebaScreen(
    onNavigateBack: () -> Unit,
    viewModel: DetallePruebaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pre-Protocolo Digital") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    uiState.prueba?.let { prueba ->
                        IconButton(onClick = {
                            val resumen = buildString {
                                appendLine("📋 COMPROBANTE TÉCNICO EN TERRENO — INDUZTEK SpA")
                                appendLine("Protocolo Ref: #${prueba.id}")
                                appendLine("Fecha/Hora: ${prueba.fecha} ${prueba.hora}")
                                appendLine("Técnico: ${prueba.tecnico}")
                                appendLine("Estado: ${prueba.estado.name}")
                                appendLine("\nVALORES MEDIDOS:")
                                prueba.mediciones.forEach { (k, v) ->
                                    val label = k.replace("_", " ").replaceFirstChar { it.uppercase() }
                                    appendLine("• $label: $v")
                                }
                                if (prueba.observaciones.isNotBlank()) {
                                    appendLine("\nObservaciones: ${prueba.observaciones}")
                                }
                                appendLine("\nNormativa aplicable: SEC Pliego Técnico RIC / IEEE Standard 43.")
                                appendLine("Certificado preliminar emitido digitalmente en faena.")
                            }

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, resumen)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Compartir comprobante al cliente"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir con el cliente")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        when {
            uiState.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            uiState.prueba == null -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { Text("Prueba no encontrada") }
            }

            else -> {
                val prueba = uiState.prueba!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ── Banner de Certificación Inmediata ──────────────────
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    "Respaldo Digital Inmediato",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Registrado en terreno. Válido como comprobante de servicio para el cliente.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // ── Encabezado del Protocolo ───────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Protocolo #${prueba.id}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        EstadoBadge(estado = prueba.estado)
                    }

                    HorizontalDivider()

                    // ── Metadatos de la Visita ─────────────────────────────
                    DetalleRow("Técnico a cargo", prueba.tecnico)
                    DetalleRow("Fecha de visita", prueba.fecha)
                    DetalleRow("Hora de ejecución", prueba.hora)
                    if (prueba.sincronizadoAt != null) {
                        DetalleRow("Sincronización remota", prueba.sincronizadoAt)
                    }

                    HorizontalDivider()

                    // ── Mediciones Registradas ─────────────────────────────
                    Text(
                        "Resultados y Mediciones Técnicas",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            prueba.mediciones.forEach { (clave, valor) ->
                                DetalleRow(
                                    label = clave.replace("_", " ").replaceFirstChar { it.uppercase() },
                                    value = valor
                                )
                            }
                        }
                    }

                    // ── Observaciones ──────────────────────────────────────
                    if (prueba.observaciones.isNotBlank()) {
                        Text(
                            "Observaciones Técnicas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = prueba.observaciones,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // ── Referencia Normativa Chilena e Internacional ──────
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Marco Normativo: Superintendencia de Electricidad y Combustibles (SEC RIC N°06/10) · IEEE Std 43/C57.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // ── Foto de Evidencia ──────────────────────────────────
                    prueba.fotoUriPath?.let { path ->
                        HorizontalDivider()
                        Text(
                            "Evidencia Fotográfica en Terreno",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Foto de evidencia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DetalleRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.55f)
        )
    }
}

@Composable
private fun EstadoBadge(estado: EstadoPrueba) {
    val (label, color) = when (estado) {
        EstadoPrueba.REGISTRADO_TERRENO -> "📍 En Terreno" to MaterialTheme.colorScheme.secondary
        EstadoPrueba.PENDIENTE_SYNC -> "⏳ Pendiente Sync" to MaterialTheme.colorScheme.tertiary
        EstadoPrueba.SINCRONIZADO -> "☁️ Sincronizado" to MaterialTheme.colorScheme.primary
        EstadoPrueba.FORMALIZADO -> "📄 Formalizado" to MaterialTheme.colorScheme.primary
    }
    AssistChip(
        onClick = {},
        label = { Text(label, fontWeight = FontWeight.SemiBold) },
        colors = AssistChipDefaults.assistChipColors(labelColor = color)
    )
}
