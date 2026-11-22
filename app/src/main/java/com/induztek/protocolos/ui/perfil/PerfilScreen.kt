package com.induztek.protocolos.ui.perfil

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.UserProfile
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.util.ImageStorageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: UserProfile?,
    profileError: String? = null,
    onVolver: () -> Unit,
    onActualizarFoto: (String?) -> Unit = {},
    onActualizarBiografia: (String?) -> Unit = {},
    onActualizarPerfilCompleto: (String, String, String?, String?) -> Unit = { _, _, _, _ -> },
    onCerrarSesion: () -> Unit = {},
    onTabSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val perfil = usuario?.getPerfil()
    val esTrabajador = perfil?.esTrabajadorInduztek ?: true
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS

    var bitmapFoto by remember(usuario?.fotoPerfil) {
        mutableStateOf(
            usuario?.fotoPerfil?.takeUnless { it.startsWith("http://") || it.startsWith("https://") }
                ?.let(ImageStorageHelper::cargarBitmap)
        )
    }

    var mostrarDialogOpcionesFoto by remember { mutableStateOf(false) }
    var mostrarDialogEditarBio by remember { mutableStateOf(false) }
    var mostrarDialogEditarPerfil by remember { mutableStateOf(false) }

    var biografiaTexto by remember(usuario?.biografia) {
        mutableStateOf(usuario?.biografia ?: "")
    }

    var nombreEdit by remember(usuario?.nombre) { mutableStateOf(usuario?.nombre ?: "") }
    var correoEdit by remember(usuario?.correo) { mutableStateOf(usuario?.correo ?: "") }
    var empresaEdit by remember(usuario?.empresaCliente) { mutableStateOf(usuario?.empresaCliente ?: "") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null && usuario != null) {
            val fileName = "profile_${usuario.id}.jpg"
            val path = ImageStorageHelper.guardarBitmapEnArchivo(context, bitmap, fileName)
            bitmapFoto = bitmap
            onActualizarFoto(path)
        }
    }

    val permissionCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error al iniciar cámara: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        } else {
            android.widget.Toast.makeText(context, "Se necesita permiso de cámara para tomar fotos", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && usuario != null) {
            val fileName = "profile_${usuario.id}.jpg"
            val path = ImageStorageHelper.guardarUriEnArchivo(context, uri, fileName)
            if (path != null) {
                bitmapFoto = ImageStorageHelper.cargarBitmap(path)
                onActualizarFoto(path)
            }
        }
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPetroleo
                )
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.Perfil.route,
                esCliente = esCliente,
                esAdminOJefe = esAdminOJefe,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            profileError?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(105.dp)
                            .clickable { mostrarDialogOpcionesFoto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (usuario?.fotoPerfil?.startsWith("http://") == true ||
                            usuario?.fotoPerfil?.startsWith("https://") == true
                        ) {
                            AsyncImage(
                                model = usuario.fotoPerfil,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(95.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape)
                            )
                        } else if (bitmapFoto != null) {
                            Image(
                                bitmap = bitmapFoto!!.asImageBitmap(),
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(95.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Color.White, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(95.dp)
                                    .clip(CircleShape)
                                    .background(InduztekBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = usuario?.nombre?.take(1)?.uppercase() ?: "U",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(NaranjaSeguridad),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.Black,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Text(
                        text = usuario?.nombre ?: "Usuario",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = perfil?.label ?: "Sin rol",
                        fontSize = 14.sp,
                        color = NaranjaSeguridad,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (esTrabajador) InduztekBlue.copy(alpha = 0.25f) else Color(0xFFFFB300).copy(alpha = 0.25f),
                                shape = RoundedCornerShape(999.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (esTrabajador) "Personal / Trabajador Induztek" else "Cliente Externo · ${usuario?.empresaCliente ?: "Empresa"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Información de cuenta editable (excepto cargo)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Información de cuenta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        IconButton(onClick = {
                            nombreEdit = usuario?.nombre ?: ""
                            correoEdit = usuario?.correo ?: ""
                            empresaEdit = usuario?.empresaCliente ?: ""
                            mostrarDialogEditarPerfil = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar datos",
                                tint = InduztekBlue
                            )
                        }
                    }

                    FilaInfoPerfil(
                        icono = Icons.Default.Email,
                        etiqueta = "Nombre y Correo",
                        valor = "${usuario?.nombre ?: "—"} (${usuario?.correo ?: "—"})",
                        textColor = Color.White
                    )

                    HorizontalDivider(color = BordeFondo)

                    FilaInfoPerfil(
                        icono = Icons.Default.Badge,
                        etiqueta = "Cargo / Rol (No modificable)",
                        valor = if (perfil != null) "${perfil.codigo} · ${perfil.label}" else "—",
                        textColor = TextoSecundario
                    )

                    if (!usuario?.empresaCliente.isNullOrBlank()) {
                        HorizontalDivider(color = BordeFondo)
                        FilaInfoPerfil(
                            icono = Icons.Default.Business,
                            etiqueta = "Empresa / Planta Asignada",
                            valor = usuario?.empresaCliente ?: "—",
                            textColor = Color.White
                        )
                    }
                }
            }

            // Biografía
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Biografía y Rol",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        IconButton(onClick = {
                            biografiaTexto = usuario?.biografia ?: ""
                            mostrarDialogEditarBio = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar biografía",
                                tint = InduztekBlue
                            )
                        }
                    }

                    Text(
                        text = if (!usuario?.biografia.isNullOrBlank()) {
                            usuario!!.biografia!!
                        } else {
                            "Sin biografía registrada. Pulsa el lápiz para añadir una descripción de tus funciones."
                        },
                        fontSize = 14.sp,
                        color = if (!usuario?.biografia.isNullOrBlank()) Color.White else TextoSecundario,
                        lineHeight = 20.sp
                    )
                }
            }

            // Cerrar Sesión
            Button(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "CERRAR SESIÓN",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }

    if (mostrarDialogOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogOpcionesFoto = false },
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Fotografía de Perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Elige cómo deseas actualizar tu fotografía de perfil:", fontSize = 13.sp, color = TextoSecundario)
                    Button(
                        onClick = {
                            mostrarDialogOpcionesFoto = false
                            val tienePermiso = androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (tienePermiso) {
                                try { cameraLauncher.launch(null) } catch (_: Exception) {}
                            } else {
                                permissionCameraLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir Cámara", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = {
                            mostrarDialogOpcionesFoto = false
                            try { galleryLauncher.launch("image/*") } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Elegir de Galería", color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogOpcionesFoto = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (mostrarDialogEditarBio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogEditarBio = false },
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Editar Biografía Profesional", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escribe una breve descripción de tu rol y experiencia:", fontSize = 13.sp, color = TextoSecundario)
                    OutlinedTextField(
                        value = biografiaTexto,
                        onValueChange = { biografiaTexto = it },
                        label = { Text("Biografía / Cargo") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onActualizarBiografia(biografiaTexto.trim())
                        mostrarDialogEditarBio = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogEditarBio = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (mostrarDialogEditarPerfil) {
        AlertDialog(
            onDismissRequest = { mostrarDialogEditarPerfil = false },
            containerColor = TarjetaFondo,
            titleContentColor = Color.White,
            textContentColor = TextoPrincipal,
            title = { Text("Editar Datos de Cuenta", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nombreEdit,
                        onValueChange = { nombreEdit = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = correoEdit,
                        onValueChange = { correoEdit = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = empresaEdit,
                        onValueChange = { empresaEdit = it },
                        label = { Text("Empresa / Planta") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text("Nota: El cargo/rol técnico no es modificable desde este panel.", fontSize = 11.sp, color = TextoSecundario)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onActualizarPerfilCompleto(nombreEdit, correoEdit, empresaEdit, usuario?.biografia)
                        mostrarDialogEditarPerfil = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InduztekBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogEditarPerfil = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun FilaInfoPerfil(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    textColor: Color = Color.White
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = InduztekBlue,
            modifier = Modifier.size(22.dp)
        )
        Column {
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                color = TextoSecundario
            )
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}
