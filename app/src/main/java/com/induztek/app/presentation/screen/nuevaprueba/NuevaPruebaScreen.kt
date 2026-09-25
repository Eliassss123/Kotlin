package com.induztek.app.presentation.screen.nuevaprueba

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.induztek.app.domain.model.TipoEquipo
import com.induztek.app.presentation.screen.nuevaprueba.state.NuevaPruebaEffect
import com.induztek.app.presentation.screen.nuevaprueba.viewmodel.NuevaPruebaViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla "Nueva Prueba" con campos dinámicos adaptados según el tipo de equipo eléctrico (SEC / IEEE).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPruebaScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetalle: (pruebaId: Long) -> Unit,
    viewModel: NuevaPruebaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Efectos one-shot ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is NuevaPruebaEffect.NavigateToDetalle ->
                    onNavigateToDetalle(effect.pruebaId)
                is NuevaPruebaEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Long)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Prueba en Terreno") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick  = { viewModel.guardarPrueba() },
                icon     = { Icon(Icons.Default.Save, contentDescription = null) },
                text     = { Text("Guardar offline") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            // ── Banner del equipo seleccionado ─────────────────────────────
            if (uiState.nombreEquipo.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier              = Modifier.padding(12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.ElectricalServices,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text  = uiState.nombreEquipo,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text  = "Formulario adaptado para: ${uiState.tipoEquipo.name.replace('_', ' ')}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // ── Técnico ────────────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.tecnico,
                onValueChange = viewModel::onTecnicoChanged,
                label         = { Text("Técnico responsable") },
                placeholder   = { Text("Ej: Ing. Carlos Mendoza") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction      = ImeAction.Next
                )
            )

            // ── Fecha y Hora en fila ───────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value         = uiState.fecha,
                    onValueChange = viewModel::onFechaChanged,
                    label         = { Text("Fecha (YYYY-MM-DD)") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value         = uiState.hora,
                    onValueChange = viewModel::onHoraChanged,
                    label         = { Text("Hora (HH:mm)") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
            }

            // ── Mediciones Técnicas Dinámicas según TipoEquipo ─────────────
            Text(
                text  = "Parámetros de Medición Específicos",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            when (uiState.tipoEquipo) {
                TipoEquipo.TRANSFORMADOR -> {
                    MedicionNumericaField(
                        label         = "Resistencia de aislamiento (IEEE 43)",
                        unidad        = "MΩ",
                        valor         = uiState.mediciones["resistencia_aislamiento"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_aislamiento", it) }
                    )
                    MedicionNumericaField(
                        label         = "Tensión de prueba inyectada",
                        unidad        = "kV",
                        valor         = uiState.mediciones["tension_prueba"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_prueba", it) }
                    )
                    MedicionNumericaField(
                        label         = "Temperatura ambiente",
                        unidad        = "°C",
                        valor         = uiState.mediciones["temperatura"] ?: "",
                        permiteNegativo = true,
                        onValueChange = { viewModel.onMedicionChanged("temperatura", it) }
                    )
                }
                TipoEquipo.RELE -> {
                    MedicionNumericaField(
                        label         = "Tiempo de disparo ante falla",
                        unidad        = "ms",
                        valor         = uiState.mediciones["tiempo_disparo"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tiempo_disparo", it) }
                    )
                    MedicionNumericaField(
                        label         = "Corriente de pickup / arranque",
                        unidad        = "A",
                        valor         = uiState.mediciones["corriente_pickup"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("corriente_pickup", it) }
                    )
                    MedicionNumericaField(
                        label         = "Tensión de alimentación auxiliar",
                        unidad        = "V",
                        valor         = uiState.mediciones["tension_control"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_control", it) }
                    )
                }
                TipoEquipo.CELDA -> {
                    MedicionNumericaField(
                        label         = "Resistencia de contactos (Ducter)",
                        unidad        = "µΩ",
                        valor         = uiState.mediciones["resistencia_contactos"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_contactos", it) }
                    )
                    MedicionNumericaField(
                        label         = "Tensión de servicio",
                        unidad        = "kV",
                        valor         = uiState.mediciones["tension_prueba"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_prueba", it) }
                    )
                    MedicionNumericaField(
                        label         = "Temperatura en barras",
                        unidad        = "°C",
                        valor         = uiState.mediciones["temperatura"] ?: "",
                        permiteNegativo = true,
                        onValueChange = { viewModel.onMedicionChanged("temperatura", it) }
                    )
                }
                TipoEquipo.CABLE -> {
                    MedicionNumericaField(
                        label         = "Aislamiento Fase a Tierra",
                        unidad        = "MΩ",
                        valor         = uiState.mediciones["resistencia_aislamiento"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_aislamiento", it) }
                    )
                    MedicionNumericaField(
                        label         = "Tensión de ensayo Hi-Pot",
                        unidad        = "kV",
                        valor         = uiState.mediciones["tension_prueba"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_prueba", it) }
                    )
                    MedicionNumericaField(
                        label         = "Temperatura",
                        unidad        = "°C",
                        valor         = uiState.mediciones["temperatura"] ?: "",
                        permiteNegativo = true,
                        onValueChange = { viewModel.onMedicionChanged("temperatura", it) }
                    )
                }
                TipoEquipo.MALLA_TIERRA -> {
                    MedicionNumericaField(
                        label         = "Resistencia de puesta a tierra (RIC N°06)",
                        unidad        = "Ω",
                        valor         = uiState.mediciones["resistencia_tierra"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_tierra", it) }
                    )
                    MedicionNumericaField(
                        label         = "Tensión de paso estimada",
                        unidad        = "V",
                        valor         = uiState.mediciones["tension_control"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_control", it) }
                    )
                    MedicionNumericaField(
                        label         = "Temperatura de terreno",
                        unidad        = "°C",
                        valor         = uiState.mediciones["temperatura"] ?: "",
                        permiteNegativo = true,
                        onValueChange = { viewModel.onMedicionChanged("temperatura", it) }
                    )
                }
                TipoEquipo.GENERADOR -> {
                    MedicionNumericaField(
                        label         = "Tensión generada Línea-Línea",
                        unidad        = "V",
                        valor         = uiState.mediciones["tension_salida"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("tension_salida", it) }
                    )
                    MedicionNumericaField(
                        label         = "Frecuencia de salida",
                        unidad        = "Hz",
                        valor         = uiState.mediciones["frecuencia"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("frecuencia", it) }
                    )
                    MedicionNumericaField(
                        label         = "Resistencia de aislamiento estator",
                        unidad        = "MΩ",
                        valor         = uiState.mediciones["resistencia_aislamiento"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_aislamiento", it) }
                    )
                }
                TipoEquipo.OTRO -> {
                    MedicionNumericaField(
                        label         = "Valor medido principal",
                        unidad        = "MΩ",
                        valor         = uiState.mediciones["resistencia_aislamiento"] ?: "",
                        onValueChange = { viewModel.onMedicionChanged("resistencia_aislamiento", it) }
                    )
                    MedicionNumericaField(
                        label         = "Temperatura",
                        unidad        = "°C",
                        valor         = uiState.mediciones["temperatura"] ?: "",
                        permiteNegativo = true,
                        onValueChange = { viewModel.onMedicionChanged("temperatura", it) }
                    )
                }
            }

            // ── Observaciones ──────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.observaciones,
                onValueChange = viewModel::onObservacionesChanged,
                label         = { Text("Observaciones del protocolo técnico") },
                placeholder   = { Text("Ej: Conexiones conformes, sin anomalías térmicas o descargas.") },
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines      = 4
            )

            // ── Captura de foto ────────────────────────────────────────────
            CapturaFotoSection(
                fotoUriPath        = uiState.fotoUriPath,
                onFotoCaptured     = viewModel::onFotoCaptured,
                onFotoRemoved      = viewModel::onFotoRemoved,
                onPermissionResult = viewModel::onCameraPermissionResult
            )

            // ── Mensaje de error ───────────────────────────────────────────
            uiState.errorMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text     = msg,
                        color    = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ── Loading ────────────────────────────────────────────────────
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(88.dp))
        }
    }
}

/**
 * Campo de texto con teclado numérico sensible y validación de decimales.
 */
@Composable
private fun MedicionNumericaField(
    label: String,
    unidad: String,
    valor: String,
    permiteNegativo: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value         = valor,
        onValueChange = { nuevoTexto ->
            val sanitized = nuevoTexto.replace(',', '.')
            val esValido = if (permiteNegativo) {
                sanitized.isEmpty() || sanitized == "-" || sanitized.matches(Regex("^-?\\d*\\.?\\d*$"))
            } else {
                sanitized.isEmpty() || sanitized.matches(Regex("^\\d*\\.?\\d*$"))
            }

            if (esValido) {
                onValueChange(sanitized)
            }
        },
        label         = { Text(label) },
        suffix        = {
            Text(
                text = unidad,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier      = Modifier.fillMaxWidth(),
        singleLine    = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction    = ImeAction.Next
        )
    )
}
