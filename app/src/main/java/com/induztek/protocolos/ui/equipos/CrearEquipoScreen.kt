// ============================================================================
// ARCHIVO : ui/equipos/CrearEquipoScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Formulario para crear equipo en terreno con datos de placa y
//           detección de duplicados en la misma instalación (RT-02).
// ============================================================================

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
import androidx.compose.material3.OutlinedTextField
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
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
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

    val tiposDisponibles = TipoEquipo.entries.map { it.nombre }

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
            // Advertencia de duplicado si aplica (RT-02)
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

            // Datos Principales
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Identificación y Ubicación",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.codigo,
                        onValueChange = { viewModel.onCodigoChanged(it) },
                        label = { Text("Código Interno / TAG Induztek *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tagCliente,
                        onValueChange = { viewModel.onTagClienteChanged(it) },
                        label = { Text("TAG Asignado por Cliente (Placa)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
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
                                .fillMaxWidth()
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
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.ubicacion,
                        onValueChange = { viewModel.onUbicacionNuevaChanged(it) },
                        label = { Text("Instalación / Subestación *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Datos de Placa de Características (FT-03 sec. 2)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Datos de Placa de Características (FT-03)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulPetroleo
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.marca,
                            onValueChange = { viewModel.onMarcaChanged(it) },
                            label = { Text("Marca") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.modelo,
                            onValueChange = { viewModel.onModeloChanged(it) },
                            label = { Text("Modelo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.numeroSerie,
                            onValueChange = { viewModel.onNumeroSerieChanged(it) },
                            label = { Text("N° Serie") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.anio,
                            onValueChange = { viewModel.onAnioChanged(it) },
                            label = { Text("Año") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.potenciaTension,
                        onValueChange = { viewModel.onPotenciaTensionChanged(it) },
                        label = { Text("Potencia (kVA / MVA)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.tensiones,
                        onValueChange = { viewModel.onTensionesChanged(it) },
                        label = { Text("Tensiones (Primario / Secundario)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.tap,
                            onValueChange = { viewModel.onTapChanged(it) },
                            label = { Text("Posición Tap") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = state.conexion,
                            onValueChange = { viewModel.onConexionChanged(it) },
                            label = { Text("Grupo Conexión") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
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
