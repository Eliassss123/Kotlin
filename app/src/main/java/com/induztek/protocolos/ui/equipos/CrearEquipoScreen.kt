






package com.induztek.protocolos.ui.equipos

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleoLight
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TarjetaFondo
import com.induztek.protocolos.viewmodel.EquipoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEquipoScreen(
    viewModel: EquipoViewModel,
    onNavigateBack: () -> Unit,
    onEquipoCreado: (String) -> Unit
) {
    val state by viewModel.nuevoEquipoState.collectAsState()
    var dropdownTipoExpanded by remember { mutableStateOf(false) }
    var dropdownConexionExpanded by remember { mutableStateOf(false) }

    val tiposDisponibles = TipoEquipo.entries.map { it.nombre }
    val opcionesConexion = listOf("Dyn1", "Dyn5", "Dyn11", "Yyn0", "YNyn0", "Dd0", "Otro")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedBorderColor = AzulPetroleoLight,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = AzulPetroleoLight,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        topBar = {
            InduztekTopBar(
                titulo = "Registrar Equipo en Terreno (RT-02)",
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
            
            if (state.advertenciaDuplicado != null) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.advertenciaDuplicado ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Identificación y Ubicación",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleoLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.codigo,
                        onValueChange = { viewModel.onCodigoChanged(it) },
                        label = {
                            Text(
                                if (state.codigo.isBlank()) "Código Interno / TAG (Obligatorio si no hay N° Serie)"
                                else "Código Interno / TAG *"
                            )
                        },
                        placeholder = { Text("Ej: TR-500KVA-01 o TAG-CAB-MT1") },
                        supportingText = {
                            Text(
                                text = "Ejemplo de TAG válido: TR-500KVA-01. Si el equipo no posee TAG, complete Marca y N° de Serie obligatoriamente.",
                                fontSize = 11.sp,
                                
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tagCliente,
                        onValueChange = { viewModel.onTagClienteChanged(it) },
                        label = { Text("TAG Asignado por Cliente (Placa)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownTipoExpanded,
                        onExpandedChange = { dropdownTipoExpanded = !dropdownTipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.tipo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Equipo *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownTipoExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = textFieldColors
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownTipoExpanded,
                            onDismissRequest = { dropdownTipoExpanded = false }
                        ) {
                            tiposDisponibles.forEach { tipoItem ->
                                DropdownMenuItem(
                                    text = { Text(tipoItem) },
                                    onClick = {
                                        viewModel.onTipoChanged(tipoItem)
                                        dropdownTipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.cliente,
                        onValueChange = { viewModel.onClienteNuevoChanged(it) },
                        label = { Text("Empresa Cliente *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.ubicacion,
                        onValueChange = { viewModel.onUbicacionNuevaChanged(it) },
                        label = { Text("Instalación / Subestación *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )
                }
            }

            
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Datos de Placa de Características (FT-03)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleoLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.marca,
                            onValueChange = { viewModel.onMarcaChanged(it) },
                            label = { Text(if (state.codigo.isBlank()) "Marca *" else "Marca") },
                            placeholder = { Text("Ej: WEG, Schneider") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.modelo,
                            onValueChange = { viewModel.onModeloChanged(it) },
                            label = { Text("Modelo") },
                            placeholder = { Text("Ej: TP-500-23") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.numeroSerie,
                            onValueChange = { viewModel.onNumeroSerieChanged(it) },
                            label = { Text(if (state.codigo.isBlank()) "N° Serie *" else "N° Serie") },
                            placeholder = { Text("Ej: SN-2024-991") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val anioInvalido = state.anio.isNotBlank() && (state.anio.length == 4 && state.anio.toIntOrNull() !in 1900..2026)
                        OutlinedTextField(
                            value = state.anio,
                            onValueChange = { viewModel.onAnioChanged(it) },
                            label = { Text("Año *") },
                            placeholder = { Text("1900-2026") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = anioInvalido,
                            supportingText = {
                                if (anioInvalido) {
                                    Text("Año no válido. Debe ser entre 1900 y 2026", color = Color.Red, fontSize = 11.sp)
                                } else if (state.anio.isNotBlank() && state.anio.length < 4) {
                                    Text("Ingrese 4 dígitos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    Text("Rango: 1900-2026", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.potenciaTension,
                        onValueChange = { viewModel.onPotenciaTensionChanged(it) },
                        label = { Text("Potencia (solo números en kVA)") },
                        placeholder = { Text("Ej: 500") },
                        suffix = { Text("kVA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tensiones,
                        onValueChange = { viewModel.onTensionesChanged(it) },
                        label = { Text("Tensiones (Primario / Secundario)") },
                        placeholder = { Text("Ej: 23 kV / 380 V") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = textFieldColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.tap,
                            onValueChange = { viewModel.onTapChanged(it) },
                            label = { Text("Posición Tap (Entero)") },
                            placeholder = { Text("Ej: 3") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            supportingText = {
                                if (state.tap == "3") {
                                    Text("Tap 3 (Nominal)", color = AzulPetroleoLight, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                } else if (state.tap.isNotBlank()) {
                                    Text("Tap ${state.tap}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    Text("Ingrese entero (ej: 3)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = dropdownConexionExpanded,
                            onExpandedChange = { dropdownConexionExpanded = !dropdownConexionExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = state.conexion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Grupo Conexión") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownConexionExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = textFieldColors
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownConexionExpanded,
                                onDismissRequest = { dropdownConexionExpanded = false }
                            ) {
                                opcionesConexion.forEach { opc ->
                                    DropdownMenuItem(
                                        text = { Text(opc) },
                                        onClick = {
                                            viewModel.onConexionChanged(opc)
                                            dropdownConexionExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (state.conexion == "Otro") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.conexionOtro,
                            onValueChange = { viewModel.onConexionOtroChanged(it) },
                            label = { Text("Especificar otro Grupo de Conexión *") },
                            placeholder = { Text("Ej: Yd11, Dz0") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors
                        )
                    }
                }
            }

            if (state.errorValidacion != null) {
                Text(
                    text = state.errorValidacion ?: "",
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    viewModel.guardarEquipoNuevo { eq ->
                        onEquipoCreado(eq.codigo)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaSeguridad),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "GUARDAR EQUIPO EN TERRENO",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
