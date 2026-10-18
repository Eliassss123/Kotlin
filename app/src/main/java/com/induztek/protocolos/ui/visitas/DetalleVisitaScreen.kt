// ============================================================================
// ARCHIVO : ui/visitas/DetalleVisitaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Gestión de protocolos de la visita, agregado de equipos (RT-03, RN-05),
//           bloqueo de duplicados en la misma visita (RN-02, Aclar. 5)
//           y cierre con emisión de acta oficial (RT-08).
// ============================================================================

package com.induztek.protocolos.ui.visitas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.Protocolo
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.EquipoViewModel
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.VisitaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleVisitaScreen(
    visitaId: String,
    viewModel: VisitaViewModel,
    equipoViewModel: EquipoViewModel,
    loginViewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onProtocoloSelected: (String) -> Unit,
    onActaGenerada: (String) -> Unit
) {
    val visita by viewModel.visitaSeleccionada.collectAsState()
    val protocolos by viewModel.protocolosVisita.collectAsState()
    val equiposDisponibles by equipoViewModel.equipos.collectAsState()
    val usuarioActivo by loginViewModel.usuarioActivo.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var showAddEquipoDialog by remember { mutableStateOf(false) }
    var equipoSeleccionadoParaProtocolo by remember { mutableStateOf<Equipo?>(null) }
    var dropdownEquiposExpanded by remember { mutableStateOf(false) }

    var showCerrarVisitaDialog by remember { mutableStateOf(false) }
    var nombreReceptorActa by remember { mutableStateOf("") }
    var errorCierreVisita by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(visitaId) {
        viewModel.cargarDetalleVisita(visitaId)
    }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Visita ${visita?.ordenTrabajo ?: ""}",
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
                .padding(16.dp)
        ) {
            visita?.let { vis ->
                // Banner de error general
                if (mensajeError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = mensajeError ?: "", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { viewModel.limpiarError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                            }
                        }
                    }
                }

                // Tarjeta 1: Información de Visita
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = "OT: ${vis.ordenTrabajo}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo
                            )
                            if (vis.cerrada) {
                                Text(
                                    text = "VISITA CERRADA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            } else {
                                Text(
                                    text = "EN TERRENO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    modifier = Modifier
                                        .background(Color(0xFFFFF3E0), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Cliente: ${vis.cliente} | Instalación: ${vis.instalacion}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Modalidad: ${vis.modalidad.label}", fontSize = 12.sp, color = AzulPetroleo, fontWeight = FontWeight.Bold)
                        Text(text = "Ingeniero a Cargo: ${vis.ingenieroACargo}", fontSize = 12.sp, color = TextoSecundario)
                        Text(text = "Técnicos: ${vis.tecnicos} | 📅 ${vis.fecha} ${vis.horaInicio}", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones de acción
                if (!vis.cerrada) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                equipoSeleccionadoParaProtocolo = equiposDisponibles.firstOrNull()
                                showAddEquipoDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Agregar Protocolo", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                errorCierreVisita = null
                                showCerrarVisitaDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cerrar Visita & Acta", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Protocolos de Equipos en esta Visita (RT-03):",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (protocolos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se han agregado protocolos a esta visita. Toque '+ Agregar Protocolo' para seleccionar un equipo.",
                            color = TextoSecundario,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(protocolos) { prot ->
                            ProtocoloItemCard(
                                protocolo = prot,
                                onClick = { onProtocoloSelected(prot.id) }
                            )
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando datos de la visita...")
                }
            }
        }
    }

    // DIÁLOGO: Agregar Protocolo para un equipo en la visita (RT-03, RN-05)
    if (showAddEquipoDialog) {
        AlertDialog(
            onDismissRequest = { showAddEquipoDialog = false },
            title = { Text(text = "Seleccionar Equipo para Protocolo", fontWeight = FontWeight.Bold, color = AzulPetroleo) },
            text = {
                Column {
                    Text(
                        text = "Al seleccionar un equipo se generarán automáticamente las pruebas obligatorias según su tipo (RN-05).",
                        fontSize = 12.sp,
                        color = TextoSecundario
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownEquiposExpanded,
                        onExpandedChange = { dropdownEquiposExpanded = !dropdownEquiposExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${equipoSeleccionadoParaProtocolo?.codigo ?: ""} [${equipoSeleccionadoParaProtocolo?.tipo ?: ""}]",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Equipo a Probar") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEquiposExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEquiposExpanded,
                            onDismissRequest = { dropdownEquiposExpanded = false }
                        ) {
                            equiposDisponibles.forEach { eq ->
                                DropdownMenuItem(
                                    text = { Text("${eq.codigo} [${eq.tagCliente}] - ${eq.tipo}") },
                                    onClick = {
                                        equipoSeleccionadoParaProtocolo = eq
                                        dropdownEquiposExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val eq = equipoSeleccionadoParaProtocolo
                        if (eq != null) {
                            val usuarioNombre = usuarioActivo?.nombre ?: "Juan Pérez"
                            viewModel.agregarEquipoAVisita(
                                equipo = eq,
                                tecnicoUsuario = usuarioNombre,
                                onProtocoloCreado = { protId ->
                                    showAddEquipoDialog = false
                                    onProtocoloSelected(protId)
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("Generar Protocolo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEquipoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO: Cerrar Visita y Generar Acta (RT-08)
    if (showCerrarVisitaDialog) {
        AlertDialog(
            onDismissRequest = { showCerrarVisitaDialog = false },
            title = { Text(text = "Cierre de Visita y Entrega de Acta (RT-08)", fontWeight = FontWeight.Bold, color = AzulPetroleo) },
            text = {
                Column {
                    Text(
                        text = "El técnico puede cerrar la visita aunque asista solo. Es obligatorio registrar el nombre de la persona del cliente que recibe el acta.",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nombreReceptorActa,
                        onValueChange = { nombreReceptorActa = it },
                        label = { Text("Nombre de quien recibe el acta *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (errorCierreVisita != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorCierreVisita ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cerrarVisitaYGenerarActa(
                            recibidoPor = nombreReceptorActa,
                            onSuccess = { acta ->
                                showCerrarVisitaDialog = false
                                onActaGenerada(acta.id)
                            },
                            onError = { err ->
                                errorCierreVisita = err
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad)
                ) {
                    Text("Emitir y Entregar Acta", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCerrarVisitaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ProtocoloItemCard(
    protocolo: Protocolo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${protocolo.codigoProtocolo} · ${protocolo.equipoCodigo}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                ResultadoChip(resultado = protocolo.resultadoGlobal)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${protocolo.tipoEquipo} · ${protocolo.pruebas.size} pruebas asignadas",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "📅 ${protocolo.fechaHora} | Estado: ${protocolo.estado.label}",
                fontSize = 11.sp,
                color = TextoSecundario
            )
        }
    }
}
