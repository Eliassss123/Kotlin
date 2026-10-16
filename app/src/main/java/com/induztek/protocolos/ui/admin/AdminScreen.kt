package com.induztek.protocolos.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    usuarioActual: UsuarioEntity? = null,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Instrumentos & Calibraciones", "Gestión de Personal (RF-22)")

    val instrumentos by adminViewModel.instrumentos.collectAsState()
    val personal by adminViewModel.personal.collectAsState()
    val nuevoState by adminViewModel.nuevoInstrumentoState.collectAsState()

    var showDialogNuevoInstrumento by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Administración Técnica (P-03 / RT-11)",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showDialogNuevoInstrumento = true },
                    containerColor = NaranjaSeguridad,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Instrumento")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FondoApp)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AzulPetroleo
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Gestión de Instrumentos y Calibraciones (RN-11, RT-11)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(instrumentos) { inst ->
                            var nuevaFecha by remember { mutableStateOf(inst.vencimientoCalibracion) }
                            var editandoCalibracion by remember { mutableStateOf(false) }
                            val vigencia = inst.estaCalibracionVigente()

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!vigencia) Color(0xFFFFEBEE) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${inst.tipo} - ${inst.marca} ${inst.modelo}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AzulPetroleo
                                        )
                                        if (!vigencia) {
                                            Text(
                                                "CALIBRACIÓN VENCIDA",
                                                color = Color(0xFFC62828),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        } else {
                                            Text(
                                                "Vigente",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))
                                    Text("N° Serie: ${inst.numeroSerie}", fontSize = 13.sp, color = Color.DarkGray)
                                    Text("Vencimiento Calibración: ${inst.vencimientoCalibracion}", fontSize = 13.sp, color = Color.DarkGray)

                                    Spacer(Modifier.height(8.dp))

                                    if (editandoCalibracion) {
                                        OutlinedTextField(
                                            value = nuevaFecha,
                                            onValueChange = { nuevaFecha = it },
                                            label = { Text("Nueva fecha vencimiento (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    adminViewModel.renovarCalibracion(inst.id, nuevaFecha)
                                                    editandoCalibracion = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                                            ) {
                                                Text("Guardar Fecha")
                                            }
                                            OutlinedButton(onClick = { editandoCalibracion = false }) {
                                                Text("Cancelar")
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { editandoCalibracion = true },
                                            modifier = Modifier.align(Alignment.End),
                                            colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Renovar Calibración", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Gestión de Personal (RF-22)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(personal) { user ->
                            val perfil = user.getPerfil()
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = AzulPetroleo)
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = user.nombre,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = AzulPetroleo
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Email: ${user.correo} · Rol: ${perfil.codigo} (${perfil.label})",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                        if (user.empresaCliente != null) {
                                            Text("Empresa: ${user.empresaCliente}", fontSize = 12.sp, color = TextoSecundario)
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = if (user.activo) "Estado: ACTIVO" else "Estado: DESACTIVADO (No puede iniciar sesión)",
                                            color = if (user.activo) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Switch(
                                        checked = user.activo,
                                        onCheckedChange = {
                                            adminViewModel.toggleActivoPersonal(user)
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF2E7D32)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialogNuevoInstrumento) {
        AlertDialog(
            onDismissRequest = { showDialogNuevoInstrumento = false },
            title = { Text("Registrar Nuevo Instrumento", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nuevoState.tipo,
                        onValueChange = { adminViewModel.onTipoChanged(it) },
                        label = { Text("Tipo de Instrumento *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoState.marca,
                        onValueChange = { adminViewModel.onMarcaChanged(it) },
                        label = { Text("Marca *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoState.modelo,
                        onValueChange = { adminViewModel.onModeloChanged(it) },
                        label = { Text("Modelo *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoState.numeroSerie,
                        onValueChange = { adminViewModel.onNumeroSerieChanged(it) },
                        label = { Text("Número de Serie *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoState.vencimientoCalibracion,
                        onValueChange = { adminViewModel.onVencimientoCalibracionChanged(it) },
                        label = { Text("Vencimiento Calibración (YYYY-MM-DD) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (nuevoState.errorValidacion != null) {
                        Text(nuevoState.errorValidacion ?: "", color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        adminViewModel.crearInstrumento {
                            showDialogNuevoInstrumento = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialogNuevoInstrumento = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
