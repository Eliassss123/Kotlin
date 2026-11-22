package com.induztek.protocolos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.RecuperacionUiState

@Composable
fun OlvideContrasenaScreen(
    viewModel: LoginViewModel,
    onCodigoEnviado: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val correo by viewModel.correoRecuperacion.collectAsState()
    val recuperacionState by viewModel.recuperacionState.collectAsState()

    LaunchedEffect(recuperacionState) {
        if (recuperacionState is RecuperacionUiState.CodigoEnviado) onCodigoEnviado()
    }

    val isEmailValid = correo.isEmpty() || viewModel.validarCorreo(correo)
    val canSubmit = correo.isNotEmpty() && viewModel.validarCorreo(correo)

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
                    imageVector = Icons.Default.LockReset,
                    contentDescription = "Recuperar Contraseña",
                    tint = NaranjaSeguridad,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Recuperar Contraseña",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ingresa tu correo electrónico registrado y te enviaremos un código de verificación.",
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { viewModel.onCorreoRecuperacionChanged(it) },
                    label = { Text("Correo Electrónico") },
                    textStyle = TextStyle(color = Color.White),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextoSecundario) },
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text(
                                text = "Ingresa un correo válido",
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
                        keyboardType = KeyboardType.Email,
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
                    onClick = { viewModel.solicitarCodigo(correo) },
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
                            text = "ENVIAR CÓDIGO",
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
