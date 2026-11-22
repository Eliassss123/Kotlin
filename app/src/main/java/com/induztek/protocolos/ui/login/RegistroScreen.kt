package com.induztek.protocolos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.RegistroUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: LoginViewModel,
    onRegistered: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var empresaPlanta by remember { mutableStateOf("Planta Lampa") }
    var dropdownEmpresaExpanded by remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }
    val state by viewModel.registroState.collectAsState()

    val empresasDisponibles = listOf("Planta Lampa", "Minera Andes", "Minera Escondida", "Codelco Ventanas", "Minera Los Pelambres", "Anglo American")

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        errorTextColor = Color.White,
        focusedContainerColor = Color(0xFF14213F),
        unfocusedContainerColor = Color(0xFF14213F),
        errorContainerColor = Color(0xFF14213F),
        cursorColor = InduztekBlue,
        focusedBorderColor = InduztekBlue,
        unfocusedBorderColor = BordeFondo,
        focusedLabelColor = InduztekBlue,
        unfocusedLabelColor = TextoSecundario
    )

    LaunchedEffect(state) {
        if (state is RegistroUiState.Success) {
            onRegistered()
            viewModel.resetRegistroState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }

                Text("Crear Cuenta Cliente (P-04)", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "Las cuentas registradas públicamente acceden en modo Cliente para consultar protocolos de su Planta.",
                    color = TextoSecundario,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo *") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextoSecundario) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico *") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextoSecundario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                // ComboBox Empresa / Planta Cliente
                ExposedDropdownMenuBox(
                    expanded = dropdownEmpresaExpanded,
                    onExpandedChange = { dropdownEmpresaExpanded = !dropdownEmpresaExpanded }
                ) {
                    OutlinedTextField(
                        value = empresaPlanta,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Empresa / Planta Cliente *") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = TextoSecundario) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownEmpresaExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = textFieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownEmpresaExpanded,
                        onDismissRequest = { dropdownEmpresaExpanded = false }
                    ) {
                        empresasDisponibles.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text(emp) },
                                onClick = {
                                    empresaPlanta = emp
                                    dropdownEmpresaExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña (min. 10 carac.) *") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextoSecundario) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    label = { Text("Confirmar Contraseña *") },
                    textStyle = TextStyle(color = Color.White),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = aceptaTerminos,
                        onCheckedChange = { aceptaTerminos = it },
                        colors = CheckboxDefaults.colors(checkedColor = InduztekBlue, uncheckedColor = TextoSecundario)
                    )
                    Text(
                        "Acepto los términos y condiciones de uso de Induztek.",
                        color = TextoSecundario,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (state is RegistroUiState.Error) {
                    Text(
                        (state as RegistroUiState.Error).message,
                        color = Color(0xFFEF5350),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (contrasena.isNotEmpty() && contrasena != confirmarContrasena) {
                    Text(
                        "Las contraseñas no coinciden",
                        color = Color(0xFFEF5350),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        if (contrasena != confirmarContrasena) return@Button
                        viewModel.registrar(
                            nombre = nombre,
                            correo = correo,
                            contrasena = contrasena,
                            empresaPlanta = empresaPlanta,
                            aceptaTerminos = aceptaTerminos
                        )
                    },
                    enabled = state !is RegistroUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InduztekBlue,
                        disabledContainerColor = Color(0xFF14213F)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (state is RegistroUiState.Loading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("REGISTRARME", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
