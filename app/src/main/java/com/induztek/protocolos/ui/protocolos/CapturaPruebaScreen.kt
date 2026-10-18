// ============================================================================
// ARCHIVO : ui/protocolos/CapturaPruebaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Formulario técnico de captura de mediciones según RT-04, RT-05, RN-06/07/08/08b/08c,
//           RN-09, RN-11 y Aclaraciones 2 y 4:
//           - Selector REALIZADA / NO REALIZADA con motivo obligatorio
//           - Campos numéricos con selector de unidad estricto (no texto libre)
//           - Trazabilidad de instrumento con advertencia de calibración vencida
//           - Mediciones PREVIA y POSTERIOR_MANTENIMIENTO
//           - Cálculos automáticos y comparación histórica (>25% resaltado)
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturaPruebaScreen(
    pruebaId: String = "",
    protocoloViewModel: ProtocoloViewModel,
    usuarioActual: UsuarioEntity? = null,
    onNavigateBack: () -> Unit
) {
    val viewModel = protocoloViewModel
    LaunchedEffect(pruebaId) {
        if (pruebaId.isNotBlank()) {
            viewModel.iniciarEdicionPrueba(pruebaId)
        }
    }
    val state by viewModel.edicionPruebaState.collectAsState()
    val instrumentosDisponibles by viewModel.instrumentosDisponibles.collectAsState()

    var dropdownInstrumentoExpanded by remember { mutableStateOf(false) }
    var mensajeErrorLocal by remember { mutableStateOf<String?>(null) }

    val unidadesComunes = listOf("GΩ", "MΩ", "kΩ", "Ω", "mΩ", "µΩ", "kV", "V", "A", "mA", "s", "ms", "%", "ratio", "°C")

    val lightTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        errorTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        errorContainerColor = Color.White,
        cursorColor = Color.Black,
        focusedBorderColor = AzulPetroleo,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = AzulPetroleo,
        unfocusedLabelColor = Color.Gray
    )

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Captura: ${state?.tipoPrueba ?: "Prueba Técnica"}",
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
            state?.let { s ->
                // TARJETA 1: Estado de Ejecución (Aclaración 2)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Estado de la Prueba (Aclaración 2)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPetroleo
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            EstadoPrueba.entries.forEach { estadoItem ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = s.estadoPrueba == estadoItem,
                                        onClick = { viewModel.onEstadoPruebaChanged(estadoItem) },
                                        colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad)
                                    )
                                    Text(
                                        text = estadoItem.label,
                                        fontSize = 13.sp,
                                        fontWeight = if (s.estadoPrueba == estadoItem) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Motivo obligatorio si NO REALIZADA
                        if (s.estadoPrueba == EstadoPrueba.NO_REALIZADA) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = s.motivoNoRealizada,
                                onValueChange = { viewModel.onMotivoNoRealizadaChanged(it) },
                                label = { Text("Motivo de No Realización * (Obligatorio)") },
                                placeholder = { Text("P. ej: Equipo no energizado, falta de accesibilidad, clima adverso...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                maxLines = 3,
                                colors = lightTextFieldColors
                            )
                        }
                    }
                }

                if (s.estadoPrueba == EstadoPrueba.REALIZADA) {
                    // TARJETA 2: Instrumento de Medición y Calibración (RN-11)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = AzulPetroleo, modifier = Modifier.height(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2. Instrumento de Medición (RN-11)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPetroleo
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            ExposedDropdownMenuBox(
                                expanded = dropdownInstrumentoExpanded,
                                onExpandedChange = { dropdownInstrumentoExpanded = !dropdownInstrumentoExpanded }
                            ) {
                                OutlinedTextField(
                                    value = s.instrumentoSeleccionado?.descripcionCompleta ?: "Seleccionar instrumento...",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Instrumento Utilizado *") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownInstrumentoExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = dropdownInstrumentoExpanded,
                                    onDismissRequest = { dropdownInstrumentoExpanded = false }
                                ) {
                                    instrumentosDisponibles.forEach { inst ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(inst.descripcionCompleta, fontWeight = FontWeight.SemiBold)
                                                    Text(
                                                        text = "Vence: ${inst.vencimientoCalibracion} (${if (inst.estaCalibracionVigente()) "Vigente" else "VENCIDA"})",
                                                        fontSize = 11.sp,
                                                        color = if (inst.estaCalibracionVigente()) Color(0xFF2E7D32) else Color.Red
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.onInstrumentoSeleccionado(inst)
                                                dropdownInstrumentoExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Advertencia o validación de calibración
                            if (s.advertenciaCalibracionVencida) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.height(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ADVERTENCIA: La calibración de este instrumento está VENCIDA (${s.instrumentoSeleccionado?.vencimientoCalibracion}). El protocolo quedará advertido para auditorías (RN-11).",
                                        fontSize = 11.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.height(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Calibración vigente hasta: ${s.instrumentoSeleccionado?.vencimientoCalibracion ?: "2026-12-31"}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }

                    // TARJETA 3: Captura de Mediciones y Unidades (RT-04, RT-05)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "3. Mediciones Técnicas (RT-04)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPetroleo
                                )
                                ResultadoChip(resultado = s.resultadoCalculado)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            s.mediciones.forEachIndexed { index, med ->
                                var valorTexto by remember(med.valorNumerico) {
                                    mutableStateOf(med.valorNumerico?.toString() ?: "")
                                }
                                var momentoActual by remember(med.momento) {
                                    mutableStateOf(med.momento)
                                }
                                var dropdownUnidadExpanded by remember { mutableStateOf(false) }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = med.campo,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AzulPetroleo
                                        )
                                        // Selector de momento PREVIA / POSTERIOR (Aclaración 4)
                                        Text(
                                            text = if (momentoActual == MomentoMedicion.POSTERIOR_MANTENIMIENTO) "Posterior Mantenimiento" else "Previa",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (momentoActual == MomentoMedicion.POSTERIOR_MANTENIMIENTO) Color(0xFF1B5E20) else Color(0xFF0277BD),
                                            modifier = Modifier
                                                .clickable {
                                                    val nuevoMomento = if (momentoActual == MomentoMedicion.PREVIA) MomentoMedicion.POSTERIOR_MANTENIMIENTO else MomentoMedicion.PREVIA
                                                    momentoActual = nuevoMomento
                                                    viewModel.actualizarMedicion(index, valorTexto.replace(",", ".").toDoubleOrNull(), med.unidad, nuevoMomento)
                                                }
                                                .background(
                                                    if (momentoActual == MomentoMedicion.POSTERIOR_MANTENIMIENTO) Color(0xFFE8F5E9) else Color(0xFFE1F5FE),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Campo de valor numérico con soporte para coma/punto
                                        OutlinedTextField(
                                            value = valorTexto,
                                            onValueChange = { nuevo ->
                                                valorTexto = nuevo
                                                val num = nuevo.replace(",", ".").toDoubleOrNull()
                                                viewModel.actualizarMedicion(index, num, med.unidad, momentoActual)
                                            },
                                            label = { Text("Valor numérico") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            modifier = Modifier.weight(1.5f),
                                            singleLine = true,
                                            colors = lightTextFieldColors
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Selector de unidad obligatorio
                                        ExposedDropdownMenuBox(
                                            expanded = dropdownUnidadExpanded,
                                            onExpandedChange = { dropdownUnidadExpanded = !dropdownUnidadExpanded },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            OutlinedTextField(
                                                value = med.unidad.ifBlank { "Unidad" },
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Unidad") },
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownUnidadExpanded) },
                                                modifier = Modifier.menuAnchor(),
                                                colors = lightTextFieldColors
                                            )
                                            ExposedDropdownMenu(
                                                expanded = dropdownUnidadExpanded,
                                                onDismissRequest = { dropdownUnidadExpanded = false }
                                            ) {
                                                unidadesComunes.forEach { u ->
                                                    DropdownMenuItem(
                                                        text = { Text(u) },
                                                        onClick = {
                                                            viewModel.actualizarMedicion(index, valorTexto.replace(",", ".").toDoubleOrNull(), u, momentoActual)
                                                            dropdownUnidadExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        IconButton(onClick = { viewModel.eliminarMedicion(index) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Gray)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // TARJETA 4: Asignación Manual si es Devanados (RN-08c) o Resultado
                    if (s.esAsignacionManual) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Asignación Manual de Resultado (RN-08c)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17)
                                )
                                Text(
                                    text = "En prueba de resistencia de devanados no hay cálculo automático: el Ingeniero asigna el resultado según equilibrio y norma.",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    listOf(ResultadoPrueba.APROBADO, ResultadoPrueba.OBSERVADO, ResultadoPrueba.RECHAZADO).forEach { resItem ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            RadioButton(
                                                selected = s.resultadoManual == resItem,
                                                onClick = { viewModel.onResultadoManualChanged(resItem) },
                                                colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad)
                                            )
                                            Text(text = resItem.label, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TARJETA 5: Comparación Histórica (RN-09, RT-06)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, tint = AzulPetroleo, modifier = Modifier.height(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Comparación con Historial (RN-09, RT-06)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPetroleo
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (s.valorAnteriorHistorico != null) {
                                Text(
                                    text = "Medición anterior registrada: ${s.valorAnteriorHistorico}",
                                    fontSize = 13.sp,
                                    color = TextoSecundario
                                )
                                if (s.variacionHistorica != null) {
                                    val varSign = if (s.variacionHistorica!! > 0) "+" else ""
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Variación: $varSign${s.variacionHistorica}%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        if (s.superaVariacion25) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "SUPERIOR AL 25% (ALERTA)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFD32F2F),
                                                modifier = Modifier
                                                    .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Primer registro para este equipo en base de datos. Se guardará como línea base para futuras visitas.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Observaciones
                    OutlinedTextField(
                        value = s.observaciones,
                        onValueChange = { viewModel.onObservacionesPruebaChanged(it) },
                        label = { Text("Observaciones técnicas y diagnóstico de prueba") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        maxLines = 3,
                        colors = lightTextFieldColors
                    )
                }

                if (mensajeErrorLocal != null) {
                    Text(text = mensajeErrorLocal ?: "", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        mensajeErrorLocal = null
                        viewModel.guardarEdicionPrueba(
                            onSuccess = onNavigateBack,
                            onError = { err -> mensajeErrorLocal = err }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "GUARDAR PRUEBA Y RECALCULAR PROTOCOLO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando datos de prueba...")
                }
            }
        }
    }
}
