// ============================================================================
// ARCHIVO : ui/login/VerificarCodigoScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : PASO 2 de recuperar contraseña: se escribe el código de 6 dígitos. Estructura igual a LoginScreen.
// ============================================================================

package com.induztek.protocolos.ui.login

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.RecuperacionUiState

// Comparte el mismo LoginViewModel.
@Composable
fun VerificarCodigoScreen(
    viewModel: LoginViewModel,
    onCodigoVerificado: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val codigoIngresado by viewModel.codigoIngresado.collectAsState()
    val correo by viewModel.correoRecuperacion.collectAsState()
    val recuperacionState by viewModel.recuperacionState.collectAsState()
    val mostrarDialog by viewModel.mostrarDialogCodigo.collectAsState()
    val codigoGenerado by viewModel.codigoGenerado.collectAsState()

    // El botón solo se habilita con exactamente 6 dígitos.
    val canSubmit = codigoIngresado.length == 6

    // Cuando el ViewModel confirma el código (CodigoVerificado), esta pantalla avisa a NavGraph para pasar a 'Nueva
    //   contraseña'.
    LaunchedEffect(recuperacionState) {
        if (recuperacionState is RecuperacionUiState.CodigoVerificado) {
            onCodigoVerificado()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulPetroleo),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            tint = AzulPetroleo
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
                    color = AzulPetroleo,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ingresa el código de 6 dígitos que fue generado para $correo",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = codigoIngresado,
                    // Limita la entrada: solo acepta el texto nuevo si tiene 6 caracteres o menos.
                    onValueChange = {
                        if (it.length <= 6) {
                            viewModel.onCodigoIngresadoChanged(it)
                        }
                    },
                    label = { Text("Código de 6 dígitos") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        // Abre el teclado numérico.
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (recuperacionState is RecuperacionUiState.Error) {
                    Text(
                        text = (recuperacionState as RecuperacionUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    // Pide al ViewModel verificar el código.
                    onClick = { viewModel.verificarCodigo(codigoIngresado) },
                    enabled = canSubmit && recuperacionState !is RecuperacionUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPetroleo,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp)
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

                // TextButton = botón sin fondo (solo texto). Reenvía un código nuevo.
                TextButton(
                    onClick = { viewModel.solicitarCodigo(correo) }
                ) {
                    Text(
                        text = "¿No recibiste el código? Reenviar",
                        color = AzulPetroleo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Diálogo que aparece al reenviar: muestra el nuevo código (simulación).
    // Modal Dialog for Local Simulation when Resending Code
    if (mostrarDialog && codigoGenerado != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogCodigo() },
            title = {
                Text(
                    text = "Nuevo Código Generado",
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
            },
            text = {
                Column {
                    Text(
                        text = "Simulación: no se envía correo real. Tu nuevo código es: $codigoGenerado",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.cerrarDialogCodigo() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("ENTENDIDO", color = Color.White)
                }
            }
        )
    }
}
