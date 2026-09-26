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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.PruebaViewModel

@Composable
fun ResumenProtocoloScreen(
    viewModel: PruebaViewModel,
    onNavigateBack: () -> Unit,
    onGuardarExito: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Resumen de Protocolo",
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
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EQUIPO: ${state.equipoCodigo}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                        EstadoChip(estado = state.estado)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Tipo de Prueba:", fontSize = 13.sp, color = TextoSecundario)
                    Text(text = state.tipoPrueba, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Ubicación / Instalación:", fontSize = 13.sp, color = TextoSecundario)
                    Text(text = state.ubicacion, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Fecha y Hora:", fontSize = 13.sp, color = TextoSecundario)
                    Text(text = state.fechaHora, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Técnico Responsable:", fontSize = 13.sp, color = TextoSecundario)
                    Text(text = state.tecnico, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                    if (state.observaciones.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Observaciones:", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = state.observaciones, fontSize = 14.sp)
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Mediciones Registradas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    state.mediciones.forEach { med ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = med.campo, fontSize = 14.sp, color = Color.DarkGray)
                            Text(text = med.valor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.guardarProtocolo {
                        onGuardarExito()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "GUARDAR PROTOCOLO (ROOM)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}
