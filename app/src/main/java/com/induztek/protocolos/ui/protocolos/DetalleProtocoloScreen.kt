// ============================================================================
// ARCHIVO : ui/protocolos/DetalleProtocoloScreen.kt
// CAPA    : View (pantalla)
// RESUMEN : Vista detallada de protocolo con lista de pruebas técnicas,
//           cálculo global (RN-10), inmutabilidad tras formalizar
//           y acción de formalización para Ingeniero (P-02) y Jefe Técnico (P-03) (RN-04, RN-16, RT-09).
// ============================================================================

package com.induztek.protocolos.ui.protocolos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.induztek.protocolos.util.ImageStorageHelper
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.PerfilUsuario
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.ui.components.EstadoChip
import com.induztek.protocolos.ui.components.InduztekTopBar
import com.induztek.protocolos.ui.components.ResultadoChip
import com.induztek.protocolos.ui.theme.AzulPetroleo
import com.induztek.protocolos.ui.theme.FondoApp
import com.induztek.protocolos.ui.theme.NaranjaSeguridad
import com.induztek.protocolos.ui.theme.TextoSecundario
import com.induztek.protocolos.viewmodel.LoginViewModel
import com.induztek.protocolos.viewmodel.ProtocoloViewModel

@Composable
fun DetalleProtocoloScreen(
    idProtocolo: String,
    viewModel: ProtocoloViewModel,
    loginViewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onEditarPrueba: (Prueba) -> Unit
) {
    val protocolo by viewModel.selectedProtocolo.collectAsState()
    val pruebas by viewModel.pruebasDelProtocolo.collectAsState()
    val usuarioActivo by loginViewModel.usuarioActivo.collectAsState()

    val perfil = usuarioActivo?.getPerfil() ?: PerfilUsuario.TECNICO_TERRENO
    val esCliente = perfil == PerfilUsuario.CLIENTE
    val puedeFormalizar = perfil.puedeFormalizar // P-02 o P-03

    var showFormalizarDialog by remember { mutableStateOf(false) }
    var esAprobacionFormalizacion by remember { mutableStateOf(true) }

    val context = LocalContext.current

    // Launcher para tomar foto con la CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        if (bitmap != null) {
            val fileName = "prot_${idProtocolo}_${System.currentTimeMillis()}.jpg"
            val path = ImageStorageHelper.guardarBitmapEnArchivo(context, bitmap, fileName)
            viewModel.agregarFotoAProtocolo(path)
        }
    }

    // Launcher para pedir permiso de cámara en tiempo de ejecución
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

    // Launcher para seleccionar imagen de la GALERÍA
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
        topBar = {
            InduztekTopBar(
                titulo = "Protocolo ${protocolo?.codigoProtocolo ?: ""}",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(FondoApp)
                .padding(16.dp)
        ) {
            protocolo?.let { prot ->
                // Tarjeta 1: Encabezado y Estado de Formalización
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPetroleo,
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

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Equipo: ${prot.equipoCodigo} (${prot.tipoEquipo})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "OT: ${prot.ordenTrabajo} | 📅 ${prot.fechaHora}", fontSize = 12.sp, color = TextoSecundario)
                        Text(text = "Técnico Creador: ${prot.tecnicoCreador}", fontSize = 12.sp, color = TextoSecundario)

                        if (prot.descripcionMantenimiento.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mantenimiento Ejecutado: ${prot.descripcionMantenimiento}",
                                fontSize = 12.sp,
                                color = AzulPetroleo,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // RN-04, RN-16: Datos de Formalización e Inmutabilidad
                        if (prot.estado == EstadoProtocolo.FORMALIZADO) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Protocolo Formalizado e Inmutable (RN-16)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "Aprobado por: ${prot.formalizadoPor ?: "Ingeniero"} el ${prot.fechaFormalizacion ?: ""}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barra de Formalización para P-02 / P-03 (si no está formalizado aún)
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Aprobar y Formalizar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                esAprobacionFormalizacion = false
                                showFormalizarDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dejar Observado", fontSize = 12.sp, color = Color(0xFFD32F2F))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Tarjeta de Fotos de Evidencia (Opcional)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AzulPetroleo, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fotos de Evidencia (${prot.fotos.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPetroleo
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

                        // Botones de Cámara y Galería para el técnico
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
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tomar Foto", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        try { galleryLauncher.launch("image/*") } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Galería", fontSize = 12.sp)
                                }
                            }
                        }

                        // Carrusel de Fotos
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
                                                    .background(Color(0xFFEEEEEE)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("Archivo", fontSize = 10.sp, color = Color.Gray)
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
                                fontSize = 11.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pruebas Técnicas del Protocolo (RN-05):",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (pruebas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No se registran pruebas en este protocolo", color = TextoSecundario)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pruebas) { prueba ->
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
                        }
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Cargando protocolo...")
                }
            }
        }
    }

    // Diálogo de Formalización (RN-04, RN-16, RT-09)
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
                        containerColor = if (esAprobacionFormalizacion) Color(0xFF2E7D32) else Color(0xFFD32F2F)
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
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = prueba.tipoPrueba,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulPetroleo,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ResultadoChip(resultado = prueba.resultado)
                    if (!esInmutable) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NaranjaSeguridad, modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (prueba.estadoPrueba == EstadoPrueba.NO_REALIZADA) {
                Text(
                    text = "NO REALIZADA - Motivo: ${prueba.motivoNoRealizada}",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Instrumento: ${prueba.instrumento} (${prueba.instrumentoSerie}) | Calibración: ${if (prueba.calibracionVigente) "Vigente" else "VENCIDA"}",
                    fontSize = 11.sp,
                    color = if (prueba.calibracionVigente) Color.DarkGray else Color.Red
                )

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(4.dp))

                prueba.mediciones.forEach { med ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${med.campo} (${med.momento.label.take(4)})", fontSize = 12.sp, color = Color.Gray)
                        Text(text = med.valorFormateado, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (prueba.observaciones.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Obs: ${prueba.observaciones}", fontSize = 11.sp, color = Color.DarkGray)
            }
        }
    }
}
