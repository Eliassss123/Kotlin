// ============================================================================
// ARCHIVO : ui/login/NuevaContrasenaScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : PASO 3 de recuperar contraseña: se define la contraseña nueva y su confirmación.
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
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

    // Dos estados LOCALES (solo visuales) para mostrar u ocultar cada campo por separado.
    var isPassVisible by remember { mutableStateOf(false) }
    var isConfirmPassVisible by remember { mutableStateOf(false) }

    // Cálculos para decidir cuándo mostrar errores.
    val isNuevaValid = nuevaContrasena.isEmpty() || viewModel.validarContrasena(nuevaContrasena)
    val areMatching = confirmarContrasena.isEmpty() || nuevaContrasena == confirmarContrasena
    // Se habilita solo si ambas contraseñas son válidas e iguales.
    val canSubmit = viewModel.validarContrasena(nuevaContrasena) &&
            viewModel.validarContrasena(confirmarContrasena) &&
            nuevaContrasena == confirmarContrasena

    // Cuando el ViewModel reporta éxito, avisa a NavGraph para volver al login.
    LaunchedEffect(recuperacionState) {
        if (recuperacionState is RecuperacionUiState.Success) {
            onContrasenaActualizada()
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
                    color = AzulPetroleo,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    // OJO: este texto está DESACTUALIZADO. La validación real (LoginViewModel.validarContrasena)
                    //      exige 8 caracteres, mayúscula, minúscula, número y símbolo.
                    text = "Crea tu nueva contraseña de al menos 6 caracteres.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Campo 1: contraseña nueva.
                // Campo Nueva Contraseña
                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = { viewModel.onNuevaContrasenaChanged(it) },
                    label = { Text("Nueva Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isPassVisible = !isPassVisible }) {
                            Icon(
                                imageVector = if (isPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPassVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = !isNuevaValid,
                    supportingText = {
                        if (!isNuevaValid) {
                            Text(
                                // OJO: mismo texto desactualizado (la regla real es más estricta).
                                text = "La contraseña debe tener al menos 6 caracteres",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo 2: repetir la contraseña. Muestra error si no coinciden (areMatching).
                // Campo Confirmar Contraseña
                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { viewModel.onConfirmarContrasenaChanged(it) },
                    label = { Text("Confirmar Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isConfirmPassVisible = !isConfirmPassVisible }) {
                            Icon(
                                imageVector = if (isConfirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isConfirmPassVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = !areMatching,
                    supportingText = {
                        if (!areMatching) {
                            Text(
                                text = "Las contraseñas no coinciden",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
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
                    // Pide al ViewModel guardar la nueva contraseña.
                    onClick = { viewModel.actualizarContrasena(nuevaContrasena) },
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
