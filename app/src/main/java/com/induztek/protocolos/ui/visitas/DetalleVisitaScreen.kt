package com.induztek.protocolos.ui.visitas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalContext
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.util.NotificationHelper
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
    val context = LocalContext.current

    var showAddEquipoDialog by remember { mutableStateOf(false) }
    var equipoSeleccionadoParaProtocolo by remember { mutableStateOf<Equipo?>(null) }
    var dropdownEquiposExpanded by remember { mutableStateOf(false) }

    var showCerrarVisitaDialog by remember { mutableStateOf(false) }
    var nombreReceptorActa by remember { mutableStateOf("") }
    var errorCierreVisita by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(visitaId) {
        viewModel.cargarDetalleVisita(visitaId)
        equipoViewModel.refresh()
        equipoViewModel.setEmpresaCliente(null)
    }

    val listaEquiposParaElegir = if (equiposDisponibles.isNotEmpty()) {
        equiposDisponibles
    } else {
        listOf(
            Equipo(
                id = "EQ-DEF-01",
                codigo = "TR-500KVA-01",
                tagCliente = "TAG-TR-01",
                tipo = "Transformador de Poder",
                cliente = "Planta Lampa",
                ubicacion = "Subestación Principal Norte",
                marca = "WEG",
                modelo = "TP-500",
                numeroSerie = "SN-2024-991",
                potenciaTension = "500 kVA"
            ),
            Equipo(
                id = "EQ-DEF-02",
                codigo = "INT-23KV-02",
                tagCliente = "TAG-INT-02",
                tipo = "Interruptor de Poder",
                cliente = "Minera Andes",
                ubicacion = "Subestación Sur",
                marca = "ABB",
                modelo = "HD4",
                numeroSerie = "SN-2024-992",
                potenciaTension = "23 kV"
            ),
            Equipo(
                id = "EQ-DEF-03",
                codigo = "CAB-MT-03",
                tagCliente = "TAG-CAB-03",
                tipo = "Cable de Poder",
                cliente = "Codelco Ventanas",
                ubicacion = "Sala Eléctrica",
                marca = "Prysmian",
                modelo = "XLPE 15kV",
                numeroSerie = "SN-2024-993",
                potenciaTension = "15 kV"
            ),
            Equipo(
                id = "EQ-DEF-04",
                codigo = "REL-51-04",
                tagCliente = "TAG-REL-04",
                tipo = "Relé de Protección",
                cliente = "Minera Escondida",
                ubicacion = "Celda MT-04",
                marca = "Siemens",
                modelo = "SIPROTEC 5",
                numeroSerie = "SN-2024-994",
                potenciaTension = "380 V"
            )
        )
    }

    Scaffold(
        containerColor = FondoApp,
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            visita?.let { vis ->

                if (mensajeError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1515))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = mensajeError ?: "", fontSize = 12.sp, color = Color(0xFFEF5350), fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { viewModel.limpiarError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.LightGray)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (vis.cerrada) {
                                Text(
                                    text = "VISITA CERRADA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F9D6B),
                                    modifier = Modifier
                                        .background(Color(0xFF0F9D6B).copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            } else {
                                Text(
                                    text = "EN TERRENO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300),
                                    modifier = Modifier
                                        .background(Color(0xFFFFB300).copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BordeFondo)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Cliente: ${vis.cliente}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
                        Text(text = "Instalación: ${vis.instalacion}", fontSize = 14.sp, color = TextoSecundario)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Modalidad: ${vis.modalidad.label}", fontSize = 13.sp, color = InduztekBlue, fontWeight = FontWeight.Bold)
                        Text(text = "Ingeniero a Cargo: ${vis.ingenieroACargo}", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = "Técnicos: ${vis.tecnicos}", fontSize = 13.sp, color = TextoSecundario)

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${vis.fecha} | ${vis.horaInicio}", fontSize = 12.sp, color = TextoSecundario)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!vis.cerrada) {
                        Button(
                            onClick = { showAddEquipoDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar Protocolo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showCerrarVisitaDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TarjetaFondo),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cerrar Visita & Acta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        Button(
                            onClick = { onActaGenerada(vis.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ver Acta Oficial de Entrega", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Protocolos de Equipos en esta Visita (RT-03):",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (protocolos.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TarjetaFondo)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No se han agregado protocolos a esta visita.\nToque 'Agregar Protocolo' para seleccionar un equipo.",
                                color = TextoSecundario,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    protocolos.forEach { protocolo ->
                        ProtocoloItemCard(protocolo = protocolo, onClick = { onProtocoloSelected(protocolo.id) })
                    }
                }
            }
        }
    }

    if (showAddEquipoDialog) {
        AlertDialog(
            onDismissRequest = { showAddEquipoDialog = false },
            title = { Text("Seleccionar Equipo para Protocolo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Seleccione el equipo registrado en terreno a probar:", fontSize = 13.sp, color = TextoSecundario)
                    ExposedDropdownMenuBox(
                        expanded = dropdownEquiposExpanded,
                        onExpandedChange = { dropdownEquiposExpanded = !dropdownEquiposExpanded }
                    ) {
                        OutlinedTextField(
                            value = equipoSeleccionadoParaProtocolo?.let { "${it.codigo} · ${it.tipo}" } ?: "Seleccionar equipo...",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEquiposExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEquiposExpanded,
                            onDismissRequest = { dropdownEquiposExpanded = false }
                        ) {
                            listaEquiposParaElegir.forEach { equipo ->
                                DropdownMenuItem(
                                    text = { Text("${equipo.codigo} (${equipo.tipo} - ${equipo.cliente})") },
                                    onClick = {
                                        equipoSeleccionadoParaProtocolo = equipo
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
                        equipoSeleccionadoParaProtocolo?.let { eq ->
                            NotificationHelper.vibrar(context, 200)
                            viewModel.agregarEquipoAVisita(eq, usuarioActivo?.nombre ?: "Técnico Induztek") { nuevoProtoId ->
                                showAddEquipoDialog = false
                                onProtocoloSelected(nuevoProtoId)
                            }
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

    if (showCerrarVisitaDialog) {
        AlertDialog(
            onDismissRequest = { showCerrarVisitaDialog = false },
            title = { Text("Cierre de Visita y Entrega de Acta (RT-08)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingrese el nombre de quien recibe el acta por parte del cliente:", fontSize = 13.sp)
                    OutlinedTextField(
                        value = nombreReceptorActa,
                        onValueChange = { nombreReceptorActa = it; errorCierreVisita = null },
                        label = { Text("Nombre de quien recibe el acta *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    errorCierreVisita?.let {
                        Text(it, color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreReceptorActa.isBlank()) {
                            errorCierreVisita = "Debe ingresar el nombre del receptor"
                        } else {
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
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("Emitir y Entregar Acta")
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
private fun ProtocoloItemCard(
    protocolo: Protocolo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${protocolo.codigoProtocolo} · ${protocolo.equipoCodigo}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = protocolo.tipoEquipo,
                    fontSize = 13.sp,
                    color = TextoSecundario
                )
            }
            ResultadoChip(resultado = protocolo.resultadoGlobal)
        }
    }
}
