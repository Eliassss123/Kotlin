// ============================================================================
// ARCHIVO : ui/equipos/DetalleEquipoScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Ficha técnica con datos de placa completos (FT-03 sec. 2),
//           indicador de evolución histórica / tendencia de empeoramiento (RT-07)
//           y listado de pruebas anteriores.
// ============================================================================

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.EquipoViewModel

@Composable
fun DetalleEquipoScreen(
    equipoId: String = "",
    codigoEquipo: String = equipoId,
    viewModel: EquipoViewModel,
    usuarioActual: com.induztek.protocolos.data.local.UsuarioEntity? = null,
    esCliente: Boolean = usuarioActual?.getPerfil() == com.induztek.protocolos.model.PerfilUsuario.CLIENTE,
    onNavigateBack: () -> Unit,
    onIniciarVisitaClick: () -> Unit = {}
) {
    val idABuscar = if (equipoId.isNotBlank()) equipoId else codigoEquipo
    val equipo by viewModel.selectedEquipo.collectAsState()
    val historialPruebas by viewModel.historialPruebas.collectAsState()

    LaunchedEffect(idABuscar) {
        viewModel.cargarDetalleEquipo(idABuscar)
    }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Ficha Técnica & Placa (FT-03)",
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
                // Tarjeta 1: Ficha Técnica y Datos de Placa
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = eq.codigo,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPetroleo
                                )
                                if (eq.tagCliente.isNotBlank()) {
                                    Text(
                                        text = "TAG Cliente: ${eq.tagCliente}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NaranjaSeguridad
                                    )
                                }
                            }
                            EstadoChip(estado = eq.ultimoEstado, esCliente = esCliente)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tipo: ${eq.tipo} (${eq.clasificacion.label})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Cliente: ${eq.cliente} | Ubicación: ${eq.ubicacion}",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )

                        // RT-07: Alerta si el equipo viene empeorando
                        if (eq.empeorando) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tendencia Histórica: Este equipo presenta degradación o variación superior al 25% respecto a mediciones previas (RT-07).",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Datos de Placa de Características (FT-03 sec. 2)
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Datos de Placa de Características",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DatoPlacaItem(etiqueta = "Marca / Modelo", valor = "${eq.marca} ${eq.modelo}".trim().ifBlank { "—" })
                            DatoPlacaItem(etiqueta = "N° de Serie", valor = eq.numeroSerie.ifBlank { "—" })
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    DatoPlacaItem(etiqueta = "Año de Fabricación", valor = eq.anio.ifBlank { "—" })
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    DatoPlacaItem(etiqueta = "Potencia", valor = eq.potenciaTension.ifBlank { "—" })
                                }
                            }
                            DatoPlacaItem(etiqueta = "Tensiones", valor = eq.tensiones.ifBlank { "—" })
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    DatoPlacaItem(etiqueta = "Grupo Conexión", valor = eq.conexion.ifBlank { "—" })
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    DatoPlacaItem(etiqueta = "Posición Tap", valor = eq.getTapEtiqueta())
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Historial de Pruebas Técnicas (RT-06, RT-07)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (historialPruebas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se registran pruebas previas para este equipo",
                            color = TextoSecundario
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(historialPruebas) { prueba ->
                            PruebaHistorialItem(prueba = prueba)
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
fun PruebaHistorialItem(prueba: Prueba) {
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                ResultadoChip(resultado = prueba.resultado)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "OT: ${prueba.ordenTrabajo} | 📅 ${prueba.fechaHora} | Estado: ${prueba.estadoPrueba.label}",
                fontSize = 11.sp,
                color = TextoSecundario
            )

            // RN-09, RT-06: Destacar variación histórica si supera 25%
            if (prueba.variacionHistoricaPorcentaje != null && prueba.variacionHistoricaPorcentaje != 0.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (prueba.superaVariacion25) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Variación histórica alta: ${if (prueba.variacionHistoricaPorcentaje > 0) "+" else ""}${prueba.variacionHistoricaPorcentaje}% (> 25%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    } else {
                        Text(
                            text = "Variación histórica: ${if (prueba.variacionHistoricaPorcentaje > 0) "+" else ""}${prueba.variacionHistoricaPorcentaje}%",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (prueba.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Obs: ${prueba.observaciones}", fontSize = 12.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(4.dp))

            prueba.mediciones.forEach { med ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = med.campo, fontSize = 12.sp, color = Color.Gray)
                    Text(text = med.valorFormateado, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DatoPlacaItem(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            fontSize = 11.sp,
            color = TextoSecundario
        )
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF263238)
        )
    }
}
