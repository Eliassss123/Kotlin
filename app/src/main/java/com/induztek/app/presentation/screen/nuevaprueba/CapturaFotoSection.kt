package com.induztek.app.presentation.screen.nuevaprueba

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable para captura de foto usando TakePicture simple.
 *
 * ── Flujo técnico ────────────────────────────────────────────────────────
 * 1. El usuario toca el botón de cámara.
 * 2. Si no tiene permiso CAMERA → solicitamos con rememberLauncherForActivityResult.
 * 3. Creamos un File vacío en Context.filesDir/images/ con nombre único.
 * 4. Generamos un content:// URI vía FileProvider (requerido Android 7+).
 * 5. Lanzamos la cámara del sistema con ACTION_IMAGE_CAPTURE apuntando a ese URI.
 * 6. La cámara escribe la foto full-resolution en ese File.
 * 7. En el callback de resultado, extraemos la ruta absoluta (file.absolutePath)
 *    y la reportamos al ViewModel via onFotoCaptured(path).
 *
 * ── ¿Por qué FileProvider? ───────────────────────────────────────────────
 * Android 7+ bloquea compartir file:// URIs entre apps por seguridad.
 * FileProvider genera un content:// URI temporal con permiso de escritura
 * que la cámara del sistema puede usar para escribir la foto.
 *
 * ── ¿Por qué guardamos la ruta absoluta y no la URI? ────────────────────
 * content:// URIs son temporales y pueden invalidarse. La ruta absoluta
 * del archivo interno es estable y se puede re-convertir en URI cuando
 * sea necesario (ej. al mostrar la foto con Coil o al subirla a Retrofit).
 *
 * @param fotoUriPath  Ruta actual de la foto (null si no hay). Del UiState.
 * @param onFotoCaptured  Callback con la ruta absoluta del archivo de foto.
 * @param onFotoRemoved   Callback para eliminar la foto del estado.
 * @param onPermissionResult  Callback con el resultado del permiso de cámara.
 */
@Composable
fun CapturaFotoSection(
    fotoUriPath: String?,
    onFotoCaptured: (absolutePath: String) -> Unit,
    onFotoRemoved: () -> Unit,
    onPermissionResult: (granted: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ── 1. URI temporal del archivo donde la cámara escribirá la foto ─────
    //    Usamos remember para no regenerar el File en cada recomposición.
    var pendingPhotoFile by remember { mutableStateOf<File?>(null) }

    // ── 2. Launcher para el resultado de la cámara ────────────────────────
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // La cámara escribió la foto en pendingPhotoFile
            pendingPhotoFile?.absolutePath?.let { path ->
                onFotoCaptured(path)
            }
        }
        // Si !success (usuario canceló), no hacemos nada — la foto anterior se mantiene
    }

    // ── 3. Launcher para solicitar permiso CAMERA ─────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        onPermissionResult(granted)
        if (granted) {
            // Permiso recién concedido → lanzar cámara directamente
            val (file, uri) = crearArchivoFoto(context)
            pendingPhotoFile = file
            cameraLauncher.launch(uri)
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────
    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = "Foto de evidencia",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        if (fotoUriPath != null) {
            // ── Foto capturada: mostrar preview + botón eliminar ──────────
            FotoPreview(
                absolutePath = fotoUriPath,
                onRemove = onFotoRemoved
            )
        } else {
            // ── Sin foto: mostrar botón de captura ────────────────────────
            BotonCapturarFoto(
                onClick = {
                    lanzarCamara(
                        context           = context,
                        permissionLauncher = permissionLauncher,
                        cameraLauncher    = cameraLauncher,
                        onFileCreated     = { file -> pendingPhotoFile = file }
                    )
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Funciones auxiliares privadas
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Determina si tenemos permiso de cámara y actúa en consecuencia.
 * Separado del Composable para mantenerlo limpio.
 */
private fun lanzarCamara(
    context: Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onFileCreated: (File) -> Unit
) {
    val tienePermiso = androidx.core.content.ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    if (tienePermiso) {
        val (file, uri) = crearArchivoFoto(context)
        onFileCreated(file)
        cameraLauncher.launch(uri)
    } else {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

/**
 * Crea un File único en Context.filesDir/images/ y retorna también
 * el content:// URI generado por FileProvider.
 *
 * Nombre del archivo: foto_YYYYMMDD_HHmmss_<millis>.jpg
 * Garantiza unicidad incluso si el técnico saca dos fotos en el mismo segundo.
 */
private fun crearArchivoFoto(context: Context): Pair<File, Uri> {
    val imagesDir = File(context.filesDir, "images").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val file = File(imagesDir, "foto_${timestamp}_${System.currentTimeMillis()}.jpg")

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",   // debe coincidir con AndroidManifest.xml
        file
    )
    return Pair(file, uri)
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BotonCapturarFoto(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = Icons.Default.CameraAlt,
                contentDescription = "Tomar foto",
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Tomar foto de evidencia",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FotoPreview(
    absolutePath: String,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Coil carga el archivo local usando su ruta absoluta
        AsyncImage(
            model              = File(absolutePath),
            contentDescription = "Foto de evidencia capturada",
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width  = 1.dp,
                    color  = MaterialTheme.colorScheme.outline,
                    shape  = RoundedCornerShape(12.dp)
                )
        )
        // Botón eliminar foto (esquina superior derecha)
        IconButton(
            onClick  = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector        = Icons.Default.Delete,
                contentDescription = "Eliminar foto",
                tint               = MaterialTheme.colorScheme.error
            )
        }
    }
}
