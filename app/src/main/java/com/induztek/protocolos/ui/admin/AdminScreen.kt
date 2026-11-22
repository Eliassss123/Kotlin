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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.Instrumento
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    usuarioActual: UserProfile? = null,
    onNavigateBack: () -> Unit,
    onTabSelected: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Instrumentos & Calibraciones", "Gestión de Personal")
    val perfil = usuarioActual?.getPerfil() ?: PerfilUsuario.JEFE_TECNICO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS

    if (esCliente) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Acceso restringido. Los clientes no tienen permisos de administración.",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    val instrumentos by adminViewModel.instrumentos.collectAsState()
    val personal by adminViewModel.personal.collectAsState()
    val nuevoState by adminViewModel.nuevoInstrumentoState.collectAsState()
    val error by adminViewModel.error.collectAsState()

    var showDialogNuevoInstrumento by remember { mutableStateOf(false) }
    var dropdownTipoInsExpanded by remember { mutableStateOf(false) }
    var dropdownMarcaInsExpanded by remember { mutableStateOf(false) }

    var showDialogNuevoUsuario by remember { mutableStateOf(false) }
    var showDeleteUserDialog by remember { mutableStateOf(false) }
    var usuarioAEliminar by remember { mutableStateOf<UserProfile?>(null) }

    var showEditRoleDialog by remember { mutableStateOf(false) }
    var usuarioAEditarRol by remember { mutableStateOf<UserProfile?>(null) }
    var nuevoRolSeleccionado by remember { mutableStateOf("Técnico de Terreno (P-01)") }
    var dropdownEditarRolExpanded by remember { mutableStateOf(false) }

    var nuevoNombreUsuario by remember { mutableStateOf("") }
    var nuevoCorreoUsuario by remember { mutableStateOf("") }
    var nuevaPasswordUsuario by remember { mutableStateOf("") }
    var cargoSeleccionado by remember { mutableStateOf("Técnico de Terreno (P-01)") }
    var dropdownCargoExpanded by remember { mutableStateOf(false) }

    var empresaPlantaSeleccionada by remember { mutableStateOf("Planta Lampa") }
    var dropdownEmpresaExpanded by remember { mutableStateOf(false) }

    val tiposInstrumento = listOf("Megóhmetro", "Microohmímetro", "Multímetro", "Pinza Amperimétrica", "Probador de Rigidez Dieléctrica")
    val marcasInstrumento = listOf("Fluke", "Megger", "AEMC", "Vanguard", "Omicron", "Hioki", "Kyoritsu")
    val cargosDisponibles = listOf("Técnico de Terreno (P-01)", "Administrador (P-03)", "Cliente (P-04)")
    val empresasDisponibles = listOf("Planta Lampa", "Minera Andes", "Minera Escondida", "Codelco Ventanas", "Minera Los Pelambres", "Induztek Central")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = InduztekBlue,
        unfocusedBorderColor = BordeFondo,
        focusedLabelColor = InduztekBlue,
        unfocusedLabelColor = TextoSecundario
    )

    LaunchedEffect(Unit) {
        adminViewModel.refresh()
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            InduztekTopBar(
                titulo = "Administración Técnica",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.Admin.route,
                esCliente = esCliente,
                esAdminOJefe = esAdminOJefe,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showDialogNuevoInstrumento = true },
                    containerColor = NaranjaSeguridad,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo Instrumento")
                }
            } else if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showDialogNuevoUsuario = true },
                    containerColor = NaranjaSeguridad,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Nueva Cuenta")
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
            error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = TarjetaFondo,
                contentColor = InduztekBlue
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selectedTab == index) Color.White else TextoSecundario
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {

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
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!vigencia) Color(0xFF3D1515) else TarjetaFondo
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
                                            color = Color.White
                                        )
                                        if (!vigencia) {
                                            Text(
                                                "CALIBRACIÓN VENCIDA",
                                                color = Color(0xFFEF5350),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        } else {
                                            Text(
                                                "Vigente",
                                                color = Color(0xFF0F9D6B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))
                                    Text("N° Serie: ${inst.numeroSerie}", fontSize = 13.sp, color = TextoSecundario)
                                    Text("Vencimiento Calibración: ${inst.vencimientoCalibracion}", fontSize = 13.sp, color = TextoSecundario)

                                    Spacer(Modifier.height(8.dp))

                                    if (editandoCalibracion) {
                                        OutlinedTextField(
                                            value = nuevaFecha,
                                            onValueChange = { nuevaFecha = it },
                                            label = { Text("Nueva fecha vencimiento (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = textFieldColors
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    adminViewModel.renovarCalibracion(inst.id, nuevaFecha)
                                                    editandoCalibracion = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Guardar Fecha")
                                            }
                                            OutlinedButton(onClick = { editandoCalibracion = false }) {
                                                Text("Cancelar", color = Color.White)
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = { editandoCalibracion = true },
                                            modifier = Modifier.align(Alignment.End),
                                            colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                                            shape = RoundedCornerShape(10.dp)
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

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(personal) { user ->
                            val perfilUser = user.getPerfil()
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
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
                                            Icon(Icons.Default.Person, contentDescription = null, tint = InduztekBlue)
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text = user.nombre,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Email: ${user.correo} · Rol: ${perfilUser.codigo} (${perfilUser.label})",
                                            fontSize = 12.sp,
                                            color = TextoSecundario
                                        )
                                        if (user.empresaCliente != null) {
                                            Text("Empresa / Planta: ${user.empresaCliente}", fontSize = 12.sp, color = TextoSecundario)
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = if (user.activo) "Estado: ACTIVO" else "Estado: DESACTIVADO (No puede iniciar sesión)",
                                            color = if (user.activo) Color(0xFF0F9D6B) else Color(0xFFEF5350),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Switch(
                                            checked = user.activo,
                                            onCheckedChange = {
                                                adminViewModel.toggleActivoPersonal(user)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFF0F9D6B)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(onClick = {
                                            usuarioAEditarRol = user
                                            nuevoRolSeleccionado = when (user.perfilCodigo) {
                                                "P-03" -> "Administrador (P-03)"
                                                "P-04" -> "Cliente (P-04)"
                                                else -> "Técnico de Terreno (P-01)"
                                            }
                                            showEditRoleDialog = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Cambiar cargo",
                                                tint = InduztekBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(2.dp))
                                        IconButton(onClick = {
                                            usuarioAEliminar = user
                                            showDeleteUserDialog = true
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar cuenta",
                                                tint = Color(0xFFEF5350),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
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
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Registrar Nuevo Instrumento", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownTipoInsExpanded,
                        onExpandedChange = { dropdownTipoInsExpanded = !dropdownTipoInsExpanded }
                    ) {
                        OutlinedTextField(
                            value = nuevoState.tipo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Instrumento *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTipoInsExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownTipoInsExpanded,
                            onDismissRequest = { dropdownTipoInsExpanded = false }
                        ) {
                            tiposInstrumento.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        adminViewModel.onTipoChanged(tipo)
                                        dropdownTipoInsExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = dropdownMarcaInsExpanded,
                        onExpandedChange = { dropdownMarcaInsExpanded = !dropdownMarcaInsExpanded }
                    ) {
                        OutlinedTextField(
                            value = nuevoState.marca,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Marca *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownMarcaInsExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownMarcaInsExpanded,
                            onDismissRequest = { dropdownMarcaInsExpanded = false }
                        ) {
                            marcasInstrumento.forEach { marca ->
                                DropdownMenuItem(
                                    text = { Text(marca) },
                                    onClick = {
                                        adminViewModel.onMarcaChanged(marca)
                                        dropdownMarcaInsExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = nuevoState.modelo,
                        onValueChange = { adminViewModel.onModeloChanged(it) },
                        label = { Text("Modelo *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = nuevoState.numeroSerie,
                        onValueChange = { adminViewModel.onNumeroSerieChanged(it) },
                        label = { Text("Número de Serie *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = nuevoState.vencimientoCalibracion,
                        onValueChange = { adminViewModel.onVencimientoCalibracionChanged(it) },
                        label = { Text("Vencimiento Calibración (YYYY-MM-DD) *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )
                    if (nuevoState.errorValidacion != null) {
                        Text(nuevoState.errorValidacion ?: "", color = Color(0xFFEF5350), fontSize = 12.sp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialogNuevoInstrumento = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (showDialogNuevoUsuario) {
        AlertDialog(
            onDismissRequest = { showDialogNuevoUsuario = false },
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Registrar Nueva Cuenta", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nuevoNombreUsuario,
                        onValueChange = { nuevoNombreUsuario = it },
                        label = { Text("Nombre Completo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = nuevoCorreoUsuario,
                        onValueChange = { nuevoCorreoUsuario = it },
                        label = { Text("Correo Electrónico *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = nuevaPasswordUsuario,
                        onValueChange = { nuevaPasswordUsuario = it },
                        label = { Text("Contraseña (min. 10 carac.) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    ExposedDropdownMenuBox(
                        expanded = dropdownCargoExpanded,
                        onExpandedChange = { dropdownCargoExpanded = !dropdownCargoExpanded }
                    ) {
                        OutlinedTextField(
                            value = cargoSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cargo / Rol *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownCargoExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownCargoExpanded,
                            onDismissRequest = { dropdownCargoExpanded = false }
                        ) {
                            cargosDisponibles.forEach { cargo ->
                                DropdownMenuItem(
                                    text = { Text(cargo) },
                                    onClick = {
                                        cargoSeleccionado = cargo
                                        dropdownCargoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // ComboBox Empresa / Planta Asignada
                    ExposedDropdownMenuBox(
                        expanded = dropdownEmpresaExpanded,
                        onExpandedChange = { dropdownEmpresaExpanded = !dropdownEmpresaExpanded }
                    ) {
                        OutlinedTextField(
                            value = empresaPlantaSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Empresa / Planta Asignada *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEmpresaExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEmpresaExpanded,
                            onDismissRequest = { dropdownEmpresaExpanded = false }
                        ) {
                            empresasDisponibles.forEach { empresa ->
                                DropdownMenuItem(
                                    text = { Text(empresa) },
                                    onClick = {
                                        empresaPlantaSeleccionada = empresa
                                        dropdownEmpresaExpanded = false
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
                        val targetRoleCode = when {
                            cargoSeleccionado.contains("P-01") -> "FIELD_TECHNICIAN"
                            cargoSeleccionado.contains("P-03") -> "ADMIN"
                            cargoSeleccionado.contains("P-04") -> "CLIENT"
                            else -> "FIELD_TECHNICIAN"
                        }
                        adminViewModel.crearUsuario(
                            nombre = nuevoNombreUsuario,
                            correo = nuevoCorreoUsuario,
                            password = nuevaPasswordUsuario,
                            empresaPlanta = empresaPlantaSeleccionada,
                            roleCode = targetRoleCode,
                            onSuccess = {
                                nuevoNombreUsuario = ""
                                nuevoCorreoUsuario = ""
                                nuevaPasswordUsuario = ""
                                showDialogNuevoUsuario = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Crear Cuenta")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialogNuevoUsuario = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (showEditRoleDialog && usuarioAEditarRol != null) {
        AlertDialog(
            onDismissRequest = { showEditRoleDialog = false },
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Cambiar Cargo / Rol", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona el nuevo cargo para ${usuarioAEditarRol?.nombre}:", fontSize = 13.sp, color = TextoSecundario)
                    ExposedDropdownMenuBox(
                        expanded = dropdownEditarRolExpanded,
                        onExpandedChange = { dropdownEditarRolExpanded = !dropdownEditarRolExpanded }
                    ) {
                        OutlinedTextField(
                            value = nuevoRolSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cargo / Rol *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEditarRolExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEditarRolExpanded,
                            onDismissRequest = { dropdownEditarRolExpanded = false }
                        ) {
                            cargosDisponibles.forEach { cargo ->
                                DropdownMenuItem(
                                    text = { Text(cargo) },
                                    onClick = {
                                        nuevoRolSeleccionado = cargo
                                        dropdownEditarRolExpanded = false
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
                        usuarioAEditarRol?.let { u ->
                            val targetRoleCode = when {
                                nuevoRolSeleccionado.contains("P-01") -> "FIELD_TECHNICIAN"
                                nuevoRolSeleccionado.contains("P-03") -> "ADMIN"
                                nuevoRolSeleccionado.contains("P-04") -> "CLIENT"
                                else -> "FIELD_TECHNICIAN"
                            }
                            adminViewModel.cambiarRolPersonal(u, targetRoleCode)
                        }
                        showEditRoleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Actualizar Rol", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditRoleDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}
