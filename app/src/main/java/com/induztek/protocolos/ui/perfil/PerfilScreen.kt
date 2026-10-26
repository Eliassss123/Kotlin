








package com.induztek.protocolos.ui.perfil

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.AzulPetroleoLight
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TarjetaFondo
import androidx.compose.material3.MaterialTheme
import com.induztek.protocolos.util.ImageStorageHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: UsuarioEntity?,
    onVolver: () -> Unit,
    onActualizarFoto: (String?) -> Unit = {},
    onActualizarBiografia: (String?) -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val context = LocalContext.current
    val perfil = usuario?.getPerfil()
    val esTrabajador = perfil?.esTrabajadorInduztek ?: true

    
    var bitmapFoto by remember(usuario?.fotoPerfil) {
        mutableStateOf(ImageStorageHelper.cargarBitmap(usuario?.fotoPerfil))
    }

    
    var mostrarDialogOpcionesFoto by remember { mutableStateOf(false) }
    var mostrarDialogEditarBio by remember { mutableStateOf(false) }
    var biografiaTexto by remember(usuario?.biografia) {
        mutableStateOf(usuario?.biografia ?: "")
    }

    
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulPetroleo)
                    .padding(vertical = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    
                    Box(
                        modifier = Modifier
                            .size(105.dp)
                            .clickable { mostrarDialogOpcionesFoto = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmapFoto != null) {
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
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Avatar por defecto",
                                    tint = Color.White,
                                    modifier = Modifier.size(76.dp)
                                )
                            }
                        }

                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NaranjaSeguridad)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
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
                                color = if (esTrabajador) Color(0xFF0277BD).copy(alpha = 0.35f) else Color(0xFFFF9800).copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (esTrabajador) Color(0xFF81D4FA) else Color(0xFFFFCC80),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (esTrabajador) "🛠️ Personal / Trabajador Induztek" else "🏢 Cliente Externo · ${usuario?.empresaCliente ?: "Empresa"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            color = AzulPetroleo
                        )
                        IconButton(onClick = {
                            biografiaTexto = usuario?.biografia ?: ""
                            mostrarDialogEditarBio = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar biografía",
                                tint = AzulPetroleo
                            )
                        }
                    }

                    Text(
                        text = if (!usuario?.biografia.isNullOrBlank()) {
                            usuario!!.biografia!!
                        } else {
                            "Sin biografía registrada. Pulsa el lápiz para añadir una descripción de tus funciones o vinculación laboral."
                        },
                        fontSize = 14.sp,
                        color = if (!usuario?.biografia.isNullOrBlank()) Color.White else Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (esTrabajador) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (esTrabajador) {
                                "✓ Cuenta habilitada para operaciones técnicas en terreno e inspección."
                            } else {
                                "✓ Cuenta cliente con acceso exclusivo a actas y protocolos de su empresa."
                            },
                            fontSize = 11.sp,
                            color = if (esTrabajador) Color(0xFF2E7D32) else Color(0xFFE65100),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))











            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    Text(
                        text = "Información de cuenta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AzulPetroleo
                    )
                
                    FilaInfoPerfil(
                        icono = Icons.Default.Email,
                        etiqueta = "Correo",
                        valor = usuario?.correo ?: "—",
                        textColor = AzulPetroleoLight
                    )

                    HorizontalDivider()

                    FilaInfoPerfil(
                        icono = Icons.Default.Badge,
                        etiqueta = "Rol",
                        valor = if (perfil != null) "${perfil.codigo} · ${perfil.label}" else "—"
                    )

                    if (!usuario?.empresaCliente.isNullOrBlank()) {
                        HorizontalDivider()
                        FilaInfoPerfil(
                            icono = Icons.Default.Business,
                            etiqueta = "Empresa",
                            valor = usuario?.empresaCliente ?: "—"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            if (perfil != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Permisos del rol",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AzulPetroleo
                        )
                        Text(
                            text = perfil.descripcion,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FilaPermiso("Registrar equipos", perfil.puedeRegistrarEquipos)
                        FilaPermiso("Registrar protocolos", perfil.puedeRegistrarProtocolos)
                        FilaPermiso("Cerrar visitas", perfil.puedeCerrarVisita)
                        FilaPermiso("Formalizar protocolos", perfil.puedeFormalizar)
                        FilaPermiso("Gestionar instrumentos", perfil.puedeGestionarInstrumentos)
                        FilaPermiso("Gestionar personal", perfil.puedeGestionarPersonal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            
            Button(
                onClick = onCerrarSesion,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    
    if (mostrarDialogOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogOpcionesFoto = false },
            title = {
                Text(text = "Foto de Perfil", fontWeight = FontWeight.Bold, color = AzulPetroleo)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Elige cómo deseas actualizar tu fotografía de perfil:")

                    Button(
                        onClick = {
                            mostrarDialogOpcionesFoto = false
                            val tienePermiso = androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (e: Exception) {
                                    android.widget.Toast.makeText(context, "No se pudo iniciar la cámara: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                permissionCameraLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Abrir Cámara")
                    }

                    OutlinedButton(
                        onClick = {
                            mostrarDialogOpcionesFoto = false
                            try {
                                galleryLauncher.launch("image/*")
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "No se pudo abrir la galería", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = AzulPetroleo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Elegir de Galería", color = AzulPetroleo)
                    }

                    if (bitmapFoto != null || !usuario?.fotoPerfil.isNullOrBlank()) {
                        Button(
                            onClick = {
                                ImageStorageHelper.eliminarArchivo(usuario?.fotoPerfil)
                                bitmapFoto = null
                                onActualizarFoto(null)
                                mostrarDialogOpcionesFoto = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color(0xFFC62828))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Quitar Foto Actual", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogOpcionesFoto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    
    if (mostrarDialogEditarBio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogEditarBio = false },
            title = {
                Text(text = "Editar Biografía Profesional", fontWeight = FontWeight.Bold, color = AzulPetroleo)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Escribe una breve descripción de tu rol, experiencia laboral o relación con Induztek:",
                        fontSize = 13.sp,
                        color = Color.White
                    )
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
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPetroleo)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogEditarBio = false }) {
                    Text("Cancelar")
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
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = AzulPetroleoLight,
            modifier = Modifier.size(22.dp)
        )
        Column {
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun FilaPermiso(
    nombre: String,
    tiene: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (tiene) Color(0xFF4CAF50) else Color(0xFFBDBDBD))
        )
        Text(
            text = nombre,
            fontSize = 13.sp,
            color = if (tiene) Color.DarkGray else Color.LightGray
        )
    }
}
