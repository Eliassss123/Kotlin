package com.induztek.protocolos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pin
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
fun VerificarCodigoScreen(
    viewModel: LoginViewModel,
    onCodigoVerificado: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val codigoIngresado by viewModel.codigoIngresado.collectAsState()
    val correo by viewModel.correoRecuperacion.collectAsState()
    val recuperacionState by viewModel.recuperacionState.collectAsState()

    val canSubmit = codigoIngresado.length == 6

    LaunchedEffect(recuperacionState) {
        if (recuperacionState is RecuperacionUiState.CodigoVerificado) {
            onCodigoVerificado()
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
                    imageVector = Icons.Default.Pin,
                    contentDescription = "Verificar Código",
                    tint = NaranjaSeguridad,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Verificar Código",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ingresa el código de 6 dígitos que fue generado para $correo",
                    fontSize = 13.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = codigoIngresado,
                    onValueChange = {
                        if (it.length <= 6) {
                            viewModel.onCodigoIngresadoChanged(it)
                        }
                    },
                    label = { Text("Código de 6 dígitos") },
                    textStyle = TextStyle(color = Color.White, fontSize = 20.sp, letterSpacing = 4.sp, textAlign = TextAlign.Center),
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
                        keyboardType = KeyboardType.Number,
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
                    onClick = { viewModel.verificarCodigo(codigoIngresado) },
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
                            text = "VERIFICAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { viewModel.solicitarCodigo(correo) }
                ) {
                    Text(
                        text = "¿No recibiste el código? Reenviar",
                        color = InduztekBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
