// ============================================================================
// ARCHIVO : ui/pruebas/NuevaPruebaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : formulario en terreno adaptado a los Anexos 1, 2 y 3:
//           1) Datos de OT y equipo (Paso 1 y 4)
//           2) Trazabilidad de instrumento y calibración vigente (PB-06)
//           3) Condiciones ambientales (Temperatura y Humedad)
//           4) Carga rápida de plantillas de medición para técnico con guantes (P-01, PB-07)
//           5) Estado y observaciones
// ============================================================================

package com.induztek.protocolos.ui.pruebas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.PruebaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPruebaScreen(
    viewModel: PruebaViewModel,
    onNavigateBack: () -> Unit,
    onIrAResumen: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val equiposDisponibles by viewModel.equiposDisponibles.collectAsState()

    var dropdownEquipoExpanded by remember { mutableStateOf(false) }
    var dropdownTipoExpanded by remember { mutableStateOf(false) }

    val tiposPruebaValidos = listOf(
        "Resistencia de Aislamiento (Megger)",
        "Relación de Transformación (TTR)",
        "Resistencia de Contacto (Microhmómetro)",
        "Inyección Secundaria de Corriente 51P",
        "Hipot VLF en Cables de MT",
        "Termografía Infrarroja"
    )

    var nuevoCampo by remember { mutableStateOf("") }
    var nuevoValor by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Registro de Prueba en Terreno",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA 1: Orden de Trabajo y Equipo
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Orden de Trabajo & Equipo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.ordenTrabajo,
                        onValueChange = { viewModel.onOrdenTrabajoChanged(it) },
                        label = { Text("Orden de Trabajo (OT)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropdown Selección Equipo
                    ExposedDropdownMenuBox(
                        expanded = dropdownEquipoExpanded,
                        onExpandedChange = { dropdownEquipoExpanded = !dropdownEquipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.equipoCodigo,
                            onValueChange = { viewModel.onEquipoCodigoChanged(it) },
                            readOnly = false,
                            label = { Text("Código de Equipo / TAG") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEquipoExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEquipoExpanded,
                            onDismissRequest = { dropdownEquipoExpanded = false }
                        ) {
                            equiposDisponibles.forEach { eq ->
                                DropdownMenuItem(
                                    text = { Text("${eq.codigo} [${eq.tagCliente}] - ${eq.tipo} (${eq.cliente})") },
                                    onClick = {
                                        viewModel.setEquipo(eq)
                                        dropdownEquipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dropdown Tipo de Prueba
                    ExposedDropdownMenuBox(
                        expanded = dropdownTipoExpanded,
                        onExpandedChange = { dropdownTipoExpanded = !dropdownTipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.tipoPrueba,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Prueba") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTipoExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownTipoExpanded,
                            onDismissRequest = { dropdownTipoExpanded = false }
                        ) {
                            tiposPruebaValidos.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        viewModel.onTipoPruebaChanged(tipo)
                                        dropdownTipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ubicacion,
                        onValueChange = { viewModel.onUbicacionChanged(it) },
                        label = { Text("Cliente / Instalación") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.tecnico,
                        onValueChange = { viewModel.onTecnicoChanged(it) },
                        label = { Text("Técnico / Responsable de Terreno") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.fechaHora,
                        onValueChange = { viewModel.onFechaHoraChanged(it) },
                        label = { Text("Fecha y Hora") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // TARJETA 2: Instrumento de Medición y Ambiente (PB-06, Paso 4)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AzulPetroleo)
                        Text(
                            text = "2. Instrumento & Condiciones Ambientales",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.instrumento,
                        onValueChange = { viewModel.onInstrumentoChanged(it) },
                        label = { Text("Instrumento de Medición (Marca / Modelo / N/S)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Calibración Vigente:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (state.calibracionVigente) "Certificado al día" else "Calibración vencida o pendiente",
                                fontSize = 12.sp,
                                color = if (state.calibracionVigente) Color(0xFF2E7D32) else Color.Red
                            )
                        }
                        Switch(
                            checked = state.calibracionVigente,
                            onCheckedChange = { viewModel.onCalibracionVigenteChanged(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2E7D32)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.temperaturaAmbiente,
                            onValueChange = { viewModel.onTemperaturaAmbienteChanged(it) },
                            label = { Text("Temp. Ambiente") },
                            leadingIcon = { Icon(Icons.Default.Thermostat, contentDescription = null) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.humedad,
                            onValueChange = { viewModel.onHumedadChanged(it) },
                            label = { Text("Humedad (%)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // TARJETA 3: Lista Dinámica de Mediciones + Botón Plantilla Rápida
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Mediciones Técnicas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                        // Botón de plantilla rápida para que el técnico con guantes no tipee
                        OutlinedButton(
                            onClick = { viewModel.cargarPlantillaRecomendada() }
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Plantilla Típica", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    state.mediciones.forEachIndexed { index, med ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.campo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = med.valorFormateado, fontSize = 13.sp, color = TextoSecundario)
                            }
                            IconButton(onClick = { viewModel.eliminarMedicion(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Agregar Medición Personalizada",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = nuevoCampo,
                            onValueChange = { nuevoCampo = it },
                            label = { Text("Parámetro/Punto") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = nuevoValor,
                            onValueChange = { nuevoValor = it },
                            label = { Text("Valor Medido") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            if (nuevoCampo.isNotBlank() && nuevoValor.isNotBlank()) {
                                viewModel.agregarMedicion(nuevoCampo, nuevoValor)
                                nuevoCampo = ""
                                nuevoValor = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ AGREGAR OTRA MEDICIÓN")
                    }
                }
            }

            // TARJETA 4: Estado y observaciones
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Diagnóstico de Terreno & Observaciones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    EstadoProtocolo.entries.forEach { estadoItem ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = (state.estado == estadoItem),
                                onClick = { viewModel.onEstadoChanged(estadoItem) },
                                colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad)
                            )
                            Text(
                                text = estadoItem.label,
                                fontSize = 14.sp,
                                fontWeight = if (state.estado == estadoItem) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.observaciones,
                        onValueChange = { viewModel.onObservacionesChanged(it) },
                        label = { Text("Observaciones y recomendaciones de campo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )
                }
            }

            // Botón Continuar a Resumen
            Button(
                onClick = onIrAResumen,
                enabled = state.equipoCodigo.isNotBlank() && state.mediciones.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "CONTINUAR A RESUMEN Y ACTA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}
