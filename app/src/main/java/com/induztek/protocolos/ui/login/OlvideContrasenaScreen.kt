// ============================================================================
// ARCHIVO : ui/login/OlvideContrasenaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : PASO 1 de recuperar contraseña: se escribe el correo y se "envía" un código.
//           Misma estructura que LoginScreen (Box > Card > Column); ver ese archivo para la explicación de cada
//             elemento.
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
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

// Pantalla que comparte el mismo LoginViewModel que Login (así los datos del flujo se conservan entre pantallas).
@Composable
fun OlvideContrasenaScreen(
    viewModel: LoginViewModel,
    onCodigoEnviado: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // Estados observados del ViewModel (ver LoginScreen para 'collectAsState').
    val correo by viewModel.correoRecuperacion.collectAsState()
    val recuperacionState by viewModel.recuperacionState.collectAsState()
    val mostrarDialog by viewModel.mostrarDialogCodigo.collectAsState()
    val codigoGenerado by viewModel.codigoGenerado.collectAsState()

    val isEmailValid = correo.isEmpty() || viewModel.validarCorreo(correo)
    // Solo se puede enviar si el correo tiene formato válido.
    val canSubmit = correo.isNotEmpty() && viewModel.validarCorreo(correo)

    // Fondo azul + tarjeta centrada, igual que en el login.
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
                // Row = fila horizontal. Aquí solo contiene la flecha de volver.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // IconButton = botón con ícono. onNavigateBack lo entrega NavGraph (hace popBackStack).
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AzulPetroleo
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
                    color = AzulPetroleo,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ingresa tu correo electrónico registrado y te enviaremos un código de verificación.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campo de correo (patrón value + onValueChange).
                OutlinedTextField(
                    value = correo,
                    onValueChange = { viewModel.onCorreoRecuperacionChanged(it) },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text(
                                text = "Ingresa un correo válido",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Si el ViewModel reportó un error (p. ej. correo no registrado) se muestra en rojo.
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
                    // Pide al ViewModel que genere el código para ese correo.
                    onClick = { viewModel.solicitarCodigo(correo) },
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

    // El diálogo se dibuja FUERA de la tarjeta, pero solo si el ViewModel lo ordena (mostrarDialog es true).
    // Modal Dialog for Local Simulation of Verification Code
    if (mostrarDialog && codigoGenerado != null) {
        // AlertDialog = ventana emergente. title / text = contenido; confirmButton = botón de acción.
        // Como no hay servidor de correo, el código se muestra aquí en pantalla (simulación).
        AlertDialog(
            // Qué hacer si el usuario toca fuera del diálogo: se cierra y se avanza a la pantalla de verificación.
            onDismissRequest = {
                viewModel.cerrarDialogCodigo()
                onCodigoEnviado()
            },
            title = {
                Text(
                    text = "Código de Recuperación",
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
            },
            text = {
                Column {
                    Text(
                        // '$codigoGenerado' dentro del texto inserta el valor de la variable (string template).
                        text = "Simulación: no se envía correo real. Tu código es: $codigoGenerado",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Usa este código en la siguiente pantalla para restablecer tu contraseña.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cerrarDialogCodigo()
                        onCodigoEnviado()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("CONTINUAR", color = Color.White)
                }
            }
        )
    }
}
