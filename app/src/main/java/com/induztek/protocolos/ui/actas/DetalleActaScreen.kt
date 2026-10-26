package com.induztek.protocolos.ui.actas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
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
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.viewmodel.VisitaViewModel

@Composable
fun DetalleActaScreen(
    actaId: String,
    visitaViewModel: VisitaViewModel,
    usuarioActual: UsuarioEntity? = null,
    onNavigateBack: () -> Unit
) {
    val actaState by visitaViewModel.actaGenerada.collectAsState()
    val protocolos by visitaViewModel.protocolosVisita.collectAsState()

    LaunchedEffect(actaId) {
        visitaViewModel.cargarActa(actaId)
    }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Acta Oficial de Entrega (RT-08)",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        val acta = actaState
        if (acta == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AzulPetroleo)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(FondoApp)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = null,
                                tint = AzulPetroleo
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "N° Acta: ${acta.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Fecha / Hora: ${acta.fechaEmision}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "Orden de Trabajo: ${acta.ordenTrabajo}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Instalación: ${acta.cliente} - ${acta.instalacion}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        if (acta.marcaPendienteFormalizacion) {
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = "★ PENDIENTE DE FORMALIZACIÓN (Validez técnica preliminar según RN-19)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Recepción en Terreno (RT-08)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Recibido por (Cliente): ${acta.recibidoPor}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Entregado por (Técnicos): ${acta.tecnicoEntrega}",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Protocolos Incluidos en esta Visita (${protocolos.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                        Spacer(Modifier.height(8.dp))
                        protocolos.forEach { proto ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        "Protocolo: ${proto.codigoProtocolo}",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                supportingContent = {
                                    Text("Equipo: ${proto.equipoCodigo} (${proto.tipoEquipo}) | Resultado: ${proto.resultadoGlobal.label}")
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }

                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Resumen y Justificación Técnica",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (acta.resumenProtocolos.isNotBlank()) acta.resumenProtocolos else "Todas las pruebas se realizaron conforme a la norma técnica.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
