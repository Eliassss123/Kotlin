package com.induztek.protocolos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.RecuperacionUiState

@Composable
fun NuevaContrasenaScreen(
    viewModel: LoginViewModel,
    onContrasenaActualizada: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val nuevaContrasena by viewModel.nuevaContrasena.collectAsState()
    val confirmarContrasena by viewModel.confirmarContrasena.collectAsState()
    val recuperacionState by viewModel.recuperacionState.collectAsState()

    var isPassVisible by remember { mutableStateOf(false) }
    var isConfirmPassVisible by remember { mutableStateOf(false) }

    val isNuevaValid = nuevaContrasena.isEmpty() || viewModel.validarContrasena(nuevaContrasena)
    val areMatching = confirmarContrasena.isEmpty() || nuevaContrasena == confirmarContrasena

    val canSubmit = viewModel.validarContrasena(nuevaContrasena) &&
            viewModel.validarContrasena(confirmarContrasena) &&
            nuevaContrasena == confirmarContrasena

    LaunchedEffect(recuperacionState) {
        if (recuperacionState is RecuperacionUiState.Success) {
            onContrasenaActualizada()
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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Nueva Contraseña",
                    tint = NaranjaSeguridad,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Nueva Contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Crea tu nueva contraseña de al menos 10 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos.",
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = { viewModel.onNuevaContrasenaChanged(it) },
                    label = { Text("Nueva Contraseña") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextoSecundario) },
                    trailingIcon = {
                        IconButton(onClick = { isPassVisible = !isPassVisible }) {
                            Icon(
                                imageVector = if (isPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPassVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = TextoSecundario
                            )
                        }
                    },
                    visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = !isNuevaValid,
                    supportingText = {
                        if (!isNuevaValid) {
                            Text(
                                text = "Debe tener al menos 10 caracteres, mayúscula, minúscula, número y símbolo",
                                color = Color(0xFFEF5350),
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
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
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { viewModel.onConfirmarContrasenaChanged(it) },
                    label = { Text("Confirmar Contraseña") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextoSecundario) },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPassVisible = !isConfirmPassVisible }) {
                            Icon(
                                imageVector = if (isConfirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isConfirmPassVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = TextoSecundario
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = !areMatching,
                    supportingText = {
                        if (!areMatching) {
                            Text(
                                text = "Las contraseñas no coinciden",
                                color = Color(0xFFEF5350),
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
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
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (recuperacionState is RecuperacionUiState.Error) {
                    Text(
                        text = (recuperacionState as RecuperacionUiState.Error).message,
                        color = Color(0xFFEF5350),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = { viewModel.actualizarContrasena(nuevaContrasena) },
                    enabled = canSubmit && recuperacionState !is RecuperacionUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = InduztekBlue,
                        disabledContainerColor = Color(0xFF14213F)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (recuperacionState is RecuperacionUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "GUARDAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
