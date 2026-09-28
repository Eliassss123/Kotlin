// ============================================================================
// ARCHIVO : ui/pruebas/NuevaPruebaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : formulario en 3 tarjetas: (1) datos generales, (2) lista dinámica de mediciones, (3) estado y
//           observaciones.
//           Todo se guarda en el estado del PruebaViewModel; aquí solo se dibuja y se avisan eventos.
// ============================================================================

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.PruebaViewModel

// @OptIn: uso una API de Material 3 marcada como 'experimental' (los menús desplegables).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPruebaScreen(
    viewModel: PruebaViewModel,
    onNavigateBack: () -> Unit,
    onIrAResumen: () -> Unit
) {
    // 'state' = el formulario completo (objeto NuevaPruebaState).
    val state by viewModel.uiState.collectAsState()
    val equiposDisponibles by viewModel.equiposDisponibles.collectAsState()

    // Estados LOCALES: ¿está abierto cada menú desplegable?
    var dropdownEquipoExpanded by remember { mutableStateOf(false) }
    var dropdownTipoExpanded by remember { mutableStateOf(false) }

    // Lista fija de tipos de prueba disponibles.
    val tiposPruebaValidos = listOf(
        "Resistencia de Aislamiento (Megger)",
        "Relación de Transformación (TTR)",
        "Resistencia de Contacto (Microhmómetro)",
        "Inyección Secundaria de Corriente 51P",
        "Hipot VLF en Cables de MT",
        "Termografía Infrarroja"
    )

    // Borradores de la nueva medición (lo escrito antes de pulsar 'Agregar'). Son locales porque aún no forman parte
    //   del formulario.
    var nuevoCampo by remember { mutableStateOf("") }
    var nuevoValor by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Registro de Nueva Prueba",
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
                // El formulario es largo: la columna se puede desplazar.
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA 1: datos generales.
            // Card Datos Generales
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Información General",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dropdown Selección Equipo
                    // Menú desplegable (como <select>): un campo de texto + un menú con opciones.
                    ExposedDropdownMenuBox(
                        expanded = dropdownEquipoExpanded,
                        onExpandedChange = { dropdownEquipoExpanded = !dropdownEquipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.equipoCodigo,
                            onValueChange = { viewModel.onEquipoCodigoChanged(it) },
                            // Este campo se puede escribir a mano, además de elegir de la lista.
                            readOnly = false,
                            label = { Text("Código de Equipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEquipoExpanded) },
                            modifier = Modifier
                                // Indica que este campo es el 'ancla' del menú (el menú se despliega debajo de él).
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownEquipoExpanded,
                            onDismissRequest = { dropdownEquipoExpanded = false }
                        ) {
                            // Una opción del menú por cada equipo.
                            equiposDisponibles.forEach { eq ->
                                // Cada opción: al tocarla, se selecciona el equipo y se cierra el menú.
                                DropdownMenuItem(
                                    text = { Text("${eq.codigo} - ${eq.tipo}") },
                                    onClick = {
                                        viewModel.setEquipo(eq)
                                        dropdownEquipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Este campo es de solo lectura (readOnly = true): solo se elige de la lista.
                    // Dropdown Tipo de Prueba
                    ExposedDropdownMenuBox(
                        expanded = dropdownTipoExpanded,
                        onExpandedChange = { dropdownTipoExpanded = !dropdownTipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.tipoPrueba,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Prueba") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTipoExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownTipoExpanded,
                            onDismissRequest = { dropdownTipoExpanded = false }
                        ) {
                            tiposPruebaValidos.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        viewModel.onTipoPruebaChanged(tipo)
                                        dropdownTipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.ubicacion,
                        onValueChange = { viewModel.onUbicacionChanged(it) },
                        label = { Text("Instalación / Ubicación") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.fechaHora,
                        onValueChange = { viewModel.onFechaHoraChanged(it) },
                        label = { Text("Fecha y Hora") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // TARJETA 2: la lista crece o se achica según lo que agregue el usuario.
            // Card Lista Dinámica de Mediciones
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Lista Dinámica de Mediciones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // forEachIndexed da el número de posición (index): se usa para saber cuál eliminar.
                    state.mediciones.forEachIndexed { index, med ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // weight(1f) = ocupa todo el espacio libre de la fila (deja el botón de borrar a la
                            //   derecha).
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.campo,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = med.valor, fontSize = 13.sp, color = TextoSecundario)
                            }
                            // Botón papelera: pide eliminar la medición de esa posición.
                            IconButton(onClick = { viewModel.eliminarMedicion(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Agregar Nueva Medición",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = nuevoCampo,
                            onValueChange = { nuevoCampo = it },
                            label = { Text("Parámetro/Campo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = nuevoValor,
                            onValueChange = { nuevoValor = it },
                            label = { Text("Valor") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón con borde. Solo agrega si ambos campos tienen texto y luego limpia los borradores.
                    OutlinedButton(
                        onClick = {
                            if (nuevoCampo.isNotBlank() && nuevoValor.isNotBlank()) {
                                viewModel.agregarMedicion(nuevoCampo, nuevoValor)
                                nuevoCampo = ""
                                nuevoValor = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ AGREGAR MEDICIÓN")
                    }
                }
            }

            // TARJETA 3: estado (radio buttons) y observaciones.
            // Card Estado del Protocolo y Observaciones
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Estado del Protocolo & Observaciones",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dibuja un RadioButton por cada estado posible del enum (así, si el enum cambia, la pantalla se
                    //   actualiza sola).
                    EstadoProtocolo.entries.forEach { estadoItem ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Botón de opción única: 'selected' indica si es el elegido actual.
                            RadioButton(
                                selected = (state.estado == estadoItem),
                                onClick = { viewModel.onEstadoChanged(estadoItem) },
                                colors = RadioButtonDefaults.colors(selectedColor = NaranjaSeguridad)
                            )
                            Text(
                                text = estadoItem.label,
                                fontSize = 14.sp,
                                fontWeight = if (state.estado == estadoItem) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.observaciones,
                        onValueChange = { viewModel.onObservacionesChanged(it) },
                        label = { Text("Observaciones de campo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )
                }
            }

            // Botón final: se habilita solo con equipo elegido y al menos una medición.
            // Botón Continuar a Resumen
            Button(
                onClick = onIrAResumen,
                enabled = state.equipoCodigo.isNotBlank() && state.mediciones.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "CONTINUAR A RESUMEN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}
