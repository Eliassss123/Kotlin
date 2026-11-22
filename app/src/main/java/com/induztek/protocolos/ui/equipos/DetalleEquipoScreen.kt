package com.induztek.protocolos.ui.equipos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.EquipoViewModel

@Composable
fun DetalleEquipoScreen(
    equipoId: String = "",
    codigoEquipo: String = equipoId,
    viewModel: EquipoViewModel,
    usuarioActual: com.induztek.protocolos.model.UserProfile? = null,
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
        containerColor = FondoApp,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            equipo?.let { eq ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    color = Color.White
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

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BordeFondo)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Tipo: ${eq.tipo} (${eq.clasificacion.label})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrincipal
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Cliente: ${eq.cliente} | Ubicación: ${eq.ubicacion}",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )

                        if (eq.empeorando) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEF5350).copy(alpha = 0.16f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tendencia Histórica: Este equipo presenta degradación o variación superior al 25% respecto a mediciones previas (RT-07).",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF5350),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BordeFondo)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Datos de Placa de Características",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleoLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF14213F), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Historial de Pruebas Técnicas (RT-06, RT-07)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (historialPruebas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TarjetaFondo)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No se registran pruebas previas para este equipo",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    historialPruebas.forEach { prueba ->
                        PruebaHistorialItem(prueba = prueba)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando datos del equipo...", color = TextoSecundario)
                }
            }
        }
    }
}

@Composable
fun PruebaHistorialItem(prueba: Prueba) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prueba.tipoPrueba,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                ResultadoChip(resultado = prueba.resultado)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "OT: ${prueba.ordenTrabajo} | ${prueba.fechaHora} | Estado: ${prueba.estadoPrueba.label}",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }

            if (prueba.variacionHistoricaPorcentaje != null && prueba.variacionHistoricaPorcentaje != 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (prueba.superaVariacion25) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Variación histórica alta: ${if (prueba.variacionHistoricaPorcentaje > 0) "+" else ""}${prueba.variacionHistoricaPorcentaje}% (> 25%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF5350)
                        )
                    } else {
                        Text(
                            text = "Variación histórica: ${if (prueba.variacionHistoricaPorcentaje > 0) "+" else ""}${prueba.variacionHistoricaPorcentaje}%",
                            fontSize = 12.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            if (prueba.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Obs: ${prueba.observaciones}", fontSize = 12.sp, color = TextoSecundario)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BordeFondo)
            Spacer(modifier = Modifier.height(8.dp))

            prueba.mediciones.forEach { med ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = med.campo, fontSize = 13.sp, color = TextoSecundario)
                    Text(text = med.valorFormateado, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
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
            color = Color.White
        )
    }
}
