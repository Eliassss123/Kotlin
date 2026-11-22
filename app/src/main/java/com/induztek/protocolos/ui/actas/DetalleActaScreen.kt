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
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.VisitaViewModel

@Composable
fun DetalleActaScreen(
    actaId: String,
    visitaViewModel: VisitaViewModel,
    usuarioActual: UserProfile? = null,
    onNavigateBack: () -> Unit
) {
    val actaState by visitaViewModel.actaGenerada.collectAsState()
    val protocolos by visitaViewModel.protocolosVisita.collectAsState()

    LaunchedEffect(actaId) {
        visitaViewModel.cargarActa(actaId)
    }

    Scaffold(
        containerColor = FondoApp,
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
                CircularProgressIndicator(color = InduztekBlue)
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = null,
                                tint = InduztekBlue
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "N° Acta: ${acta.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Fecha / Hora: ${acta.fechaEmision}",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )
                        Text(
                            text = "Orden de Trabajo: ${acta.ordenTrabajo}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrincipal
                        )
                        Text(
                            text = "Instalación: ${acta.cliente} - ${acta.instalacion}",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )

                        if (acta.marcaPendienteFormalizacion) {
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFB300).copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "PENDIENTE DE FORMALIZACIÓN (Validez técnica preliminar según RN-19)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFFB300),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
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
                        Text(
                            "Recepción en Terreno (RT-08)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleoLight
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextoSecundario)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Recibido por (Cliente): ${acta.recibidoPor}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextoPrincipal
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Entregado por (Técnicos): ${acta.tecnicoEntrega}",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Protocolos Incluidos en esta Visita (${protocolos.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleoLight
                        )
                        Spacer(Modifier.height(8.dp))
                        protocolos.forEach { proto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF0F9D6B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            "Protocolo: ${proto.codigoProtocolo}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            "Equipo: ${proto.equipoCodigo} (${proto.tipoEquipo})",
                                            fontSize = 12.sp,
                                            color = TextoSecundario
                                        )
                                    }
                                }
                                Text(
                                    proto.resultadoGlobal.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F9D6B)
                                )
                            }
                            HorizontalDivider(color = BordeFondo)
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
                        Text(
                            "Resumen y Justificación Técnica",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleoLight
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (acta.resumenProtocolos.isNotBlank()) acta.resumenProtocolos else "Todas las pruebas se realizaron conforme a la norma técnica.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }
            }
        }
    }
}
