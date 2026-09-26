package com.induztek.protocolos.ui.equipos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.EquipoViewModel

@Composable
fun DetalleEquipoScreen(
    codigoEquipo: String,
    viewModel: EquipoViewModel,
    onNavigateBack: () -> Unit,
    onNuevaPruebaClick: (String) -> Unit
) {
    val equipo by viewModel.selectedEquipo.collectAsState()
    val historialPruebas by viewModel.historialPruebas.collectAsState()

    LaunchedEffect(codigoEquipo) {
        viewModel.cargarDetalleEquipo(codigoEquipo)
    }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Detalle de Equipo",
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
        ) {
            equipo?.let { eq ->
                // Tarjeta de información del equipo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eq.codigo,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo
                            )
                            EstadoChip(estado = eq.ultimoEstado)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tipo: ${eq.tipo}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Ubicación: ${eq.ubicacion}",
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onNuevaPruebaClick(eq.codigo) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "NUEVA PRUEBA A ESTE EQUIPO",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Historial de Pruebas Anteriores",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (historialPruebas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay pruebas registradas para este equipo",
                            color = TextoSecundario
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(historialPruebas) { prueba ->
                            PruebaHistorialCard(prueba = prueba)
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando datos del equipo...")
                }
            }
        }
    }
}

@Composable
fun PruebaHistorialCard(prueba: Prueba) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prueba.tipoPrueba,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                EstadoChip(estado = prueba.estado)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${prueba.fechaHora}",
                fontSize = 13.sp,
                color = TextoSecundario
            )

            if (prueba.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Obs: ${prueba.observaciones}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Valores Medidos:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPetroleo
            )

            prueba.mediciones.forEach { med ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = med.campo, fontSize = 13.sp, color = Color.Gray)
                    Text(text = med.valor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
