package com.induztek.protocolos.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageStorageHelper {

    fun guardarBitmapEnArchivo(context: Context, bitmap: Bitmap, nombreArchivo: String): String {
        val file = File(context.filesDir, nombreArchivo)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return file.absolutePath
    }

    fun guardarUriEnArchivo(context: Context, uri: Uri, nombreArchivo: String): String? {
        val file = File(context.filesDir, nombreArchivo)
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun eliminarArchivo(ruta: String?): Boolean {
        if (ruta.isNullOrBlank()) return false
        val file = File(ruta)
        return if (file.exists()) file.delete() else false
    }

    fun cargarBitmap(ruta: String?): Bitmap? {
        if (ruta.isNullOrBlank()) return null
        return try {
            val file = File(ruta)
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
        } catch (e: Exception) {
            null
        }
    }
}
