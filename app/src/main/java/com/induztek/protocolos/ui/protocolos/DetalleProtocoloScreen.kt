package com.induztek.protocolos.ui.protocolos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekBottomBar
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.navigation.Routes
import com.induztek.protocolos.ui.theme.*
import com.induztek.protocolos.util.ImageStorageHelper
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProtocoloScreen(
    idProtocolo: String,
    viewModel: ProtocoloViewModel,
    loginViewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onEditarPrueba: (Prueba) -> Unit,
    onTabSelected: (String) -> Unit = {}
) {
    val protocolo by viewModel.selectedProtocolo.collectAsState()
    val pruebas by viewModel.pruebasDelProtocolo.collectAsState()
    val usuarioActivo by loginViewModel.usuarioActivo.collectAsState()

    val perfil = usuarioActivo?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val puedeFormalizar = perfil.puedeFormalizar
    val esAdminOJefe = perfil == PerfilUsuario.JEFE_TECNICO || perfil == PerfilUsuario.INGENIERO_PRUEBAS

    var showFormalizarDialog by remember { mutableStateOf(false) }
    var esAprobacionFormalizacion by remember { mutableStateOf(true) }

    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        if (bitmap != null) {
            val fileName = "prot_${idProtocolo}_${System.currentTimeMillis()}.jpg"
            val path = ImageStorageHelper.guardarBitmapEnArchivo(context, bitmap, fileName)
            viewModel.agregarFotoAProtocolo(path)
        }
    }

    val permissionCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (_: Exception) {}
        } else {
            android.widget.Toast.makeText(context, "Se requiere permiso de cámara para tomar fotos", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            val fileName = "prot_${idProtocolo}_${System.currentTimeMillis()}.jpg"
            val path = ImageStorageHelper.guardarUriEnArchivo(context, uri, fileName)
            if (path != null) {
                viewModel.agregarFotoAProtocolo(path)
            }
        }
    }

    LaunchedEffect(idProtocolo) {
        viewModel.cargarProtocolo(idProtocolo)
    }

    Scaffold(
        containerColor = FondoApp,
        topBar = {
            InduztekTopBar(
                titulo = "Protocolo ${protocolo?.codigoProtocolo ?: ""}",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            InduztekBottomBar(
                currentRoute = Routes.ListaProtocolos.route,
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
                .background(FondoApp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            protocolo?.let { prot ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prot.codigoProtocolo,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ResultadoChip(resultado = prot.resultadoGlobal)
                                EstadoChip(estado = prot.estado, esCliente = esCliente)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BordeFondo)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "Equipo: ${prot.equipoCodigo} (${prot.tipoEquipo})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "OT: ${prot.ordenTrabajo} | ${prot.fechaHora}", fontSize = 12.sp, color = TextoSecundario)
                        }
                        Text(text = "Técnico Creador: ${prot.tecnicoCreador}", fontSize = 12.sp, color = TextoSecundario)

                        if (prot.descripcionMantenimiento.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Mantenimiento Ejecutado: ${prot.descripcionMantenimiento}",
                                fontSize = 12.sp,
                                color = InduztekBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (prot.estado == EstadoProtocolo.FORMALIZADO) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F9D6B).copy(alpha = 0.16f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0F9D6B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Protocolo Formalizado e Inmutable (RN-16)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F9D6B)
                                    )
                                    Text(
                                        text = "Aprobado por: ${prot.formalizadoPor ?: "Ingeniero"} el ${prot.fechaFormalizacion ?: ""}",
                                        fontSize = 11.sp,
                                        color = TextoSecundario
                                    )
                                }
                            }
                        }
                    }
                }

                if (puedeFormalizar && prot.estado != EstadoProtocolo.FORMALIZADO) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                esAprobacionFormalizacion = true
                                showFormalizarDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D6B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aprobar y Formalizar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                esAprobacionFormalizacion = false
                                showFormalizarDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dejar Observado", fontSize = 12.sp, color = Color(0xFFEF5350))
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = InduztekBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fotos de Evidencia (${prot.fotos.size})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            if (!prot.esInmutable && !esCliente) {
                                Text(
                                    text = "Opcional",
                                    fontSize = 11.sp,
                                    color = TextoSecundario
                                )
                            }
                        }

                        if (!prot.esInmutable && !esCliente) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
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
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tomar Foto", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try { galleryLauncher.launch("image/*") } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galería", fontSize = 12.sp)
                                }
                            }
                        }

                        if (prot.fotos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(prot.fotos) { index, fotoPath ->
                                    val bitmap = remember(fotoPath) {
                                        ImageStorageHelper.cargarBitmap(fotoPath)
                                    }
                                    Box(modifier = Modifier.size(76.dp)) {
                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = "Foto ${index + 1}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(76.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(76.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF14213F)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Archivo", fontSize = 10.sp, color = TextoSecundario)
                                            }
                                        }
                                        if (!prot.esInmutable && !esCliente) {
                                            IconButton(
                                                onClick = { viewModel.eliminarFotoDeProtocolo(index) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(22.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sin fotografías adjuntas aún. Presione 'Tomar Foto' o 'Galería' para adjuntar evidencia técnica.",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pruebas Técnicas del Protocolo (RN-05):",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (pruebas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = TarjetaFondo)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No se registran pruebas en este protocolo",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    pruebas.forEach { prueba ->
                        PruebaDetalleCard(
                            prueba = prueba,
                            esInmutable = prot.esInmutable || esCliente,
                            onClick = {
                                if (!prot.esInmutable && !esCliente) {
                                    viewModel.iniciarEdicionPrueba(prueba)
                                    onEditarPrueba(prueba)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = InduztekBlue)
                }
            }
        }
    }

    if (showFormalizarDialog) {
        AlertDialog(
            onDismissRequest = { showFormalizarDialog = false },
            title = {
                Text(
                    text = if (esAprobacionFormalizacion) "Aprobar y Formalizar Protocolo" else "Observar Protocolo",
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )
            },
            text = {
                Column {
                    Text(
                        text = if (esAprobacionFormalizacion)
                            "Al formalizar el protocolo, éste pasa a estado FORMALIZADO e INMUTABLE. Quedará registrado su nombre (${usuarioActivo?.nombre}) y fecha oficial (RN-04, RN-16)."
                        else
                            "El protocolo quedará en estado OBSERVADO para que el técnico creador subsane los puntos pendientes (RN-17).",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val nombreFormalizador = "${usuarioActivo?.nombre ?: "Ingeniero"} (${perfil.label})"
                        viewModel.formalizarProtocolo(
                            usuarioFormalizador = nombreFormalizador,
                            esAprobacion = esAprobacionFormalizacion,
                            onSuccess = { showFormalizarDialog = false }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (esAprobacionFormalizacion) Color(0xFF0F9D6B) else Color(0xFFEF5350)
                    )
                ) {
                    Text(if (esAprobacionFormalizacion) "Confirmar Formalización" else "Confirmar Observación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormalizarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PruebaDetalleCard(
    prueba: Prueba,
    esInmutable: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !esInmutable) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TarjetaFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = prueba.tipoPrueba,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ResultadoChip(resultado = prueba.resultado)
                    if (!esInmutable) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NaranjaSeguridad, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (prueba.estadoPrueba == EstadoPrueba.NO_REALIZADA) {
                Text(
                    text = "NO REALIZADA - Motivo: ${prueba.motivoNoRealizada}",
                    fontSize = 13.sp,
                    color = Color(0xFFEF5350),
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Instrumento: ${prueba.instrumento} (${prueba.instrumentoSerie}) | Calibración: ${if (prueba.calibracionVigente) "Vigente" else "VENCIDA"}",
                    fontSize = 12.sp,
                    color = if (prueba.calibracionVigente) TextoSecundario else Color(0xFFEF5350)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BordeFondo)
                Spacer(modifier = Modifier.height(8.dp))

                prueba.mediciones.forEach { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${med.campo} (${med.momento.label.take(4)})", fontSize = 13.sp, color = TextoSecundario)
                        Text(text = med.valorFormateado, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    }
                }
            }

            if (prueba.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Obs: ${prueba.observaciones}", fontSize = 12.sp, color = TextoSecundario)
            }
        }
    }
}
