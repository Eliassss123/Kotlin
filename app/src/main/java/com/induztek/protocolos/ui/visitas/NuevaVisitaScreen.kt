







package com.induztek.protocolos.ui.visitas

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.ModalidadVisita
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.viewmodel.VisitaViewModel

@Composable
fun NuevaVisitaScreen(
    visitaViewModel: VisitaViewModel,
    usuarioActual: com.induztek.protocolos.data.local.UsuarioEntity? = null,
    onNavigateBack: () -> Unit,
    onVisitaCreada: (String) -> Unit
) {
    val viewModel = visitaViewModel
    val state by viewModel.nuevaVisitaState.collectAsState()

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Crear Visita Técnica (RT-01)",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Orden de Trabajo y Lugar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ordenTrabajo,
                        onValueChange = { viewModel.onOrdenTrabajoChanged(it) },
                        label = { Text("Orden de Trabajo (OT) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.cliente,
                        onValueChange = { viewModel.onClienteChanged(it) },
                        label = { Text("Empresa Cliente *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.instalacion,
                        onValueChange = { viewModel.onInstalacionChanged(it) },
                        label = { Text("Instalación / Subestación / Planta *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Modalidad de la Visita (Aclaración 4)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ModalidadVisita.entries.forEach { mod ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = state.modalidad == mod,
                                onClick = { viewModel.onModalidadChanged(mod) },
                                colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad)
                            )
                            Column {
                                Text(
                                    text = mod.label,
                                    fontSize = 14.sp,
                                    fontWeight = if (state.modalidad == mod) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(text = mod.descripcion, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Responsables y Condiciones",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ingenieroACargo,
                        onValueChange = { viewModel.onIngenieroACargoChanged(it) },
                        label = { Text("Ingeniero a Cargo * (Obligatorio, Aclar. 4 y 7)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tecnicos,
                        onValueChange = { viewModel.onTecnicosChanged(it) },
                        label = { Text("Técnicos de Terreno Asistentes") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.temperaturaAmbiente,
                            onValueChange = { viewModel.onTemperaturaAmbienteChanged(it) },
                            label = { Text("Temp. Ambiente") },
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

            if (state.errorValidacion != null) {
                Text(
                    text = state.errorValidacion ?: "",
                    color = Color.Red,
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
                colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                shape = RoundedCornerShape(8.dp)
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
