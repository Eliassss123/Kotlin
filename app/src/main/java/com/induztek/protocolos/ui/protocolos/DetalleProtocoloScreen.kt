// ============================================================================
// ARCHIVO : ui/protocolos/DetalleProtocoloScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : vista de SOLO LECTURA de un protocolo guardado (no se puede editar).
// ============================================================================

package com.induztek.protocolos.ui.protocolos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

// El id llega por la ruta de navegación.
@Composable
fun DetalleProtocoloScreen(
    idProtocolo: String,
    viewModel: ProtocoloViewModel,
    onNavigateBack: () -> Unit
) {
    val protocolo by viewModel.selectedProtocolo.collectAsState()

    // Al abrir la pantalla, pide al ViewModel cargar ese protocolo.
    LaunchedEffect(idProtocolo) {
        viewModel.cargarProtocolo(idProtocolo)
    }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Vista Protocolo Guardado",
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
                // Hace que la columna se pueda desplazar. rememberScrollState() guarda la posición del scroll.
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Si el protocolo ya cargó, dibuja los datos; si es null, cae en '?: run' (mensaje de carga).
            protocolo?.let { prot ->
                // Primera tarjeta: datos generales del protocolo.
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
                                text = prot.codigoProtocolo,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo
                            )
                            EstadoChip(estado = prot.estado)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Código de Equipo:", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = prot.equipoCodigo, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Tipo de Equipo / Prueba:", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = prot.tipoEquipo, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Fecha y Hora de Registro:", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = prot.fechaHora, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Técnico Certificador:", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = prot.tecnico, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                        if (prot.observaciones.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Observaciones de Terreno:", fontSize = 13.sp, color = TextoSecundario)
                            Text(text = prot.observaciones, fontSize = 14.sp)
                        }
                    }
                }

                // Segunda tarjeta: lista de mediciones guardadas.
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Mediciones Guardadas (Solo Lectura)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Una fila por medición, separadas con línea divisoria.
                        prot.mediciones.forEach { med ->
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
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando datos del protocolo...")
                }
            }
        }
    }
}
