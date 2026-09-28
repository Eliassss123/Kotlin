// ============================================================================
// ARCHIVO : ui/login/LoginScreen.kt
// CAPA    : View (pantalla) - ¡LA PANTALLA MÁS EXPLICADA! Las demás reutilizan las mismas ideas.
//
// COMPOSE NO USA HTML NI XML. La pantalla se describe con FUNCIONES de Kotlin marcadas con @Composable.
// Se parece a HTML porque también se anidan elementos, pero es código Kotlin normal:
//
//   HTML / CSS                          COMPOSE
//   <div> (columna)                     Column { ... }
//   <div> (fila)                        Row { ... }
//   <div> apilando capas                Box { ... }
//   <p> / <span>                        Text("...")
//   <button>                            Button(onClick = { ... }) { ... }
//   <input>                             OutlinedTextField(value, onValueChange)
//   <div> con sombra                    Card { ... }
//   margin / espacio vacío              Spacer(Modifier.height(10.dp))
//   CSS (padding, width, background)    Modifier.padding(...).fillMaxWidth().background(...)
//   lista con scroll                    LazyColumn { items(...) }
//   if renderizado condicional          if (...) { ... } normal de Kotlin
//
// Los paréntesis ( ) llevan los "atributos" y las llaves { } llevan los "hijos".
// La interfaz se REDIBUJA sola cada vez que cambia un estado que la pantalla observa.
// ============================================================================

package com.induztek.protocolos.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Email
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
import com.induztek.protocolos.viewmodel.LoginUiState
import com.induztek.protocolos.viewmodel.LoginViewModel

// @Composable = "esta función dibuja interfaz". Funciona como un componente web propio.
// Parámetros:
//  - viewModel: la lógica y los datos (ver LoginViewModel)
//  - onLoginSuccess / onOlvideContrasenaClick: son 'lambdas' (funciones que la pantalla ejecuta cuando pasa algo).
//    La pantalla no sabe navegar: avisa y NavGraph decide a dónde ir.
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onOlvideContrasenaClick: () -> Unit
) {
    // collectAsState() convierte un StateFlow del ViewModel en un 'State' de Compose.
    // 'by' permite usar 'email' como un texto normal. Cada vez que el ViewModel cambia el valor, Compose vuelve a
    //   ejecutar
    // esta función y redibuja (recomposición). Es parecido a useState/useSelector en React.
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val mensajeExito by viewModel.mensajeExitoLogin.collectAsState()

    // Estado LOCAL de la pantalla (solo visual: ¿se ve la contraseña?).
    // mutableStateOf = valor observable; remember = lo conserva entre redibujados (si no, volvería a 'false' cada
    //   vez).
    // No va al ViewModel porque no es lógica de negocio.
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Cálculos simples para decidir qué mostrar: no se marca error mientras el campo está vacío.
    val isEmailValid = email.isEmpty() || viewModel.validarCorreo(email)
    val isPasswordValid = password.isEmpty() || viewModel.validarContrasena(password)
    val passwordErrorMsg = viewModel.mensajeValidacionContrasena(password)
    // El botón 'Iniciar sesión' solo se habilita si ambos campos son válidos.
    val canSubmit = email.isNotEmpty() && password.isNotEmpty() && viewModel.validarCorreo(email) && viewModel.validarContrasena(password)

    // LaunchedEffect = ejecuta código como 'efecto secundario' (no dibuja nada). Se vuelve a ejecutar cuando cambia
    //   uiState.
    // Aquí: si el login fue exitoso, avisa a NavGraph (onLoginSuccess) y limpia el estado.
    // Va dentro de LaunchedEffect (y no directo en el cuerpo de la función) para que ocurra UNA vez y no en cada
    //   redibujado.
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Box apila hijos uno sobre otro. Aquí es el FONDO azul a pantalla completa.
    // Modifier encadena 'estilos' con puntos: .fillMaxSize() (100% de ancho y alto) .background(color).
    // contentAlignment = Center: centra la tarjeta.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulPetroleo),
        contentAlignment = Alignment.Center
    ) {
        // Card = tarjeta blanca con esquinas redondeadas (shape) y sombra (elevation). fillMaxWidth(0.92f) = 92% del
        //   ancho.
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            // Column apila los hijos en VERTICAL. padding(24.dp) = espacio interno.
            // 'dp' = unidad de medida independiente de la densidad de pantalla (como px en CSS, pero adaptable).
            // horizontalAlignment centra los hijos en horizontal.
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon dibuja un ícono vectorial de la librería material-icons. tint = color; size = tamaño.
                //   contentDescription = texto para lectores de pantalla.
                Icon(
                    imageVector = Icons.Default.ElectricalServices,
                    contentDescription = "Logo Induztek",
                    tint = NaranjaSeguridad,
                    modifier = Modifier.size(60.dp)
                )

                // Spacer = espacio vacío de 10dp entre elementos (como un margin).
                Spacer(modifier = Modifier.height(10.dp))

                // Text muestra texto. fontSize se mide en 'sp' (unidad de tamaño de letra).
                Text(
                    text = "INDUZTEK",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
                Text(
                    text = "Protocolos de Prueba en Terreno",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // En Compose los if/else son 'renderizado condicional': si hay mensaje de éxito (tras cambiar la
                //   contraseña) se dibuja este
                // bloque; si no, no existe. 'mensajeExito!!' = aseguro que no es null.
                if (mensajeExito != null) {
                    Text(
                        text = mensajeExito!!,
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // El campo de correo. Ver la explicación del patrón 'value + onValueChange' justo debajo.
                // Campo Correo Electrónico
                // OutlinedTextField = <input> con borde. PATRÓN CLAVE de Compose:
                //  value         = qué texto mostrar (viene del ViewModel)
                //  onValueChange = qué hacer cuando el usuario escribe ('it' = el texto nuevo)
                // El campo NO guarda el texto por sí mismo: cada tecla llama al ViewModel, este actualiza el estado y
                //   Compose redibuja con el valor nuevo.
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Correo Electrónico") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    // isError pinta el campo en rojo. supportingText es el texto de ayuda bajo el campo (aquí, el
                    //   mensaje de error).
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
                    // Qué teclado se abre (Email trae la @) y qué hace la tecla Enter: Next = pasa al siguiente
                    //   campo, Done = termina.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // El campo de contraseña: igual que el de correo, con ojo para mostrar/ocultar.
                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    // Ícono a la DERECHA (leadingIcon = a la izquierda). Es un botón que alterna isPasswordVisible
                    //   (ojo abierto/cerrado).
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    // PasswordVisualTransformation() muestra puntos (••••) en lugar de letras.
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = !isPasswordValid,
                    supportingText = {
                        if (!isPasswordValid && passwordErrorMsg != null) {
                            Text(
                                text = passwordErrorMsg,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        } else if (!isPasswordValid && password.isNotEmpty()) {
                            Text(
                                text = "Contraseña inválida",
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

                Spacer(modifier = Modifier.height(4.dp))

                // Texto tocable para recuperar la contraseña.
                // ¿Olvidaste tu contraseña?
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = AzulPetroleo,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.End)
                        // clickable vuelve tocable cualquier elemento (como onclick). Al tocar: prepara el flujo en
                        //   el ViewModel y avisa a NavGraph para navegar.
                        .clickable {
                            viewModel.prepararRecuperacion()
                            onOlvideContrasenaClick()
                        }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 'is' comprueba el tipo del estado actual. Si es Error, se muestra el mensaje debajo (se lee con
                //   'as' = convertir al tipo Error).
                if (uiState is LoginUiState.Error) {
                    Text(
                        text = (uiState as LoginUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Button: onClick = qué pasa al tocar (llamar a viewModel.login()). 'enabled' controla si se puede
                //   pulsar:
                // solo con formulario válido y sin estar cargando. Lo de adentro { } es el contenido del botón.
                Button(
                    onClick = { viewModel.login() },
                    enabled = canSubmit && uiState !is LoginUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPetroleo,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    // Dentro del botón: si está cargando muestra una ruedita (CircularProgressIndicator); si no, el
                    //   texto 'INICIAR SESIÓN'.
                    if (uiState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "INICIAR SESIÓN",
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
