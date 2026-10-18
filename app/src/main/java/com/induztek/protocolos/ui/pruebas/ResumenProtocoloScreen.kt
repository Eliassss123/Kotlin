// ============================================================================
// ARCHIVO : ui/pruebas/ResumenProtocoloScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : revisión completa de la prueba y generación de Acta de Respaldo en Terreno
//           (solución al problema PB-01 de Anexo 3: cliente se va con respaldo escrito).
// ============================================================================

package com.induztek.protocolos.ui.pruebas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.util.ImageStorageHelper
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
                titulo = "Resumen & Acta de Terreno",
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
            // TARJETA 1: Encabezado corporativo Induztek SpA y Datos Generales
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricalServices,
                                contentDescription = null,
                                tint = NaranjaSeguridad,
                                modifier = Modifier.height(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INDUZTEK INGENIERÍA SpA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo
                            )
                        }
                        EstadoChip(estado = state.estado)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Orden de Trabajo (OT):", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.ordenTrabajo, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Equipo:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.equipoCodigo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AzulPetroleo)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Tipo de Prueba:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.tipoPrueba, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Cliente / Ubicación:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.ubicacion, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Fecha y Hora:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.fechaHora, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Técnico Certificador:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.tecnico, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            // TARJETA 2: Trazabilidad de Instrumento y Calibración (PB-06)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Trazabilidad de Instrumento & Ambiente",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Instrumento Utilizado:", fontSize = 12.sp, color = TextoSecundario)
                    Text(text = state.instrumento, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Estado de Calibración: ", fontSize = 12.sp, color = TextoSecundario)
                        if (state.calibracionVigente) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "VIGENTE (Apta para auditoría)", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "OBSERVADA / NO VIGENTE", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Temperatura: ${state.temperaturaAmbiente}", fontSize = 13.sp)
                        Text(text = "Humedad: ${state.humedad}", fontSize = 13.sp)
                    }
                }
            }

            // TARJETA 3: Mediciones Registradas
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Mediciones Registradas en Terreno",
                        fontSize = 15.sp,
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
                            Text(text = med.campo, fontSize = 13.sp, color = Color.DarkGray)
                            Text(text = med.valorFormateado, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color(0xFFF5F5F5))
                    }

                    if (state.observaciones.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Observaciones de Campo:", fontSize = 12.sp, color = TextoSecundario)
                        Text(text = state.observaciones, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }

            // TARJETA DE EVIDENCIA FOTOGRÁFICA (Opcional, si existen fotos)
            if (state.fotos.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Evidencia Fotográfica (${state.fotos.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(state.fotos) { index, fotoPath ->
                                val bitmap = remember(fotoPath) {
                                    ImageStorageHelper.cargarBitmap(fotoPath)
                                }
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TARJETA 4: Solución PB-01: Acta de Entrega Inmediata al Cliente
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Acta de Respaldo Inmediato (PB-01)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Al guardar, este informe queda disponible de inmediato para el cliente en el sistema. Constancia formal requerida antes de reenergizar la instalación.",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

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
                    text = "GUARDAR PROTOCOLO Y GENERAR ACTA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
        }
    }
}
