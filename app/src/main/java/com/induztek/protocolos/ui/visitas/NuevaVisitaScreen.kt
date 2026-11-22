package com.induztek.protocolos.ui.visitas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.VisitaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaVisitaScreen(
    visitaViewModel: VisitaViewModel,
    usuarioActual: UserProfile? = null,
    onNavigateBack: () -> Unit,
    onVisitaCreada: (String) -> Unit,
    onTabSelected: (String) -> Unit = {}
) {
    val viewModel = visitaViewModel
    val state by viewModel.nuevaVisitaState.collectAsState()
    val perfil = usuarioActual?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = AzulPetroleoLight,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = AzulPetroleoLight,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            InduztekTopBar(
                titulo = "Crear Visita Técnica (RT-01)",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.ListaVisitas.route,
                esCliente = esCliente,
                esAdminOJefe = esAdminOJefe,
                onTabSelected = onTabSelected
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Orden de Trabajo y Lugar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleoLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ordenTrabajo,
                        onValueChange = { viewModel.onOrdenTrabajoChanged(it) },
                        label = { Text("Orden de Trabajo (OT) *") },
                        placeholder = { Text("Ej: OT-2026-085") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.cliente,
                        onValueChange = { viewModel.onClienteChanged(it) },
                        label = { Text("Empresa Cliente *") },
                        placeholder = { Text("Ej: Planta Lampa") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.instalacion,
                        onValueChange = { viewModel.onInstalacionChanged(it) },
                        label = { Text("Instalación / Subestación / Planta *") },
                        placeholder = { Text("Ej: Subestación Principal Norte") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Modalidad de la Visita (Aclaración 4)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleoLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ModalidadVisita.entries.forEach { mod ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = state.modalidad == mod,
                                onClick = { viewModel.onModalidadChanged(mod) },
                                colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad, unselectedColor = TextoSecundario)
                            )
                            Column {
                                Text(
                                    text = mod.label,
                                    fontSize = 14.sp,
                                    fontWeight = if (state.modalidad == mod) FontWeight.Bold else FontWeight.Normal,
                                    color = TextoPrincipal
                                )
                                Text(text = mod.descripcion, fontSize = 11.sp, color = TextoSecundario)
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Responsables y Condiciones",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleoLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ingenieroACargo,
                        onValueChange = { viewModel.onIngenieroACargoChanged(it) },
                        label = { Text("Ingeniero a Cargo * (Obligatorio, Aclar. 4 y 7)") },
                        placeholder = { Text("Ej: Carlos Mendoza") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tecnicos,
                        onValueChange = { viewModel.onTecnicosChanged(it) },
                        label = { Text("Técnicos de Terreno Asistentes") },
                        placeholder = { Text("Ej: Juan Pérez") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.temperaturaAmbiente,
                            onValueChange = { viewModel.onTemperaturaAmbienteChanged(it) },
                            label = { Text("Temp. Ambiente") },
                            placeholder = { Text("22°C") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.humedad,
                            onValueChange = { viewModel.onHumedadChanged(it) },
                            label = { Text("Humedad (%)") },
                            placeholder = { Text("45%") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                    }
                }
            }

            if (state.errorValidacion != null) {
                Text(
                    text = state.errorValidacion ?: "",
                    color = Color(0xFFEF5350),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    viewModel.crearVisita { visitaId ->
                        onVisitaCreada(visitaId)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "INICIAR VISITA EN TERRENO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
