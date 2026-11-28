package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.MediaApi
import com.induztek.protocolos.data.remote.requireBody
import java.io.File
import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/** Sube imágenes locales al backend. Errores: MED-001 (fuera de filesDir), MED-002 (> 10 MB), MED-003 (no se borró la copia). */
class MediaRepository(
    private val api: MediaApi,
    private val appFilesDir: File
) {
    suspend fun uploadIfLocal(path: String?): String? {
        if (path.isNullOrBlank() || path.startsWith("https://") || path.startsWith("http://")) return path
        val file = File(path).canonicalFile
        val root = appFilesDir.canonicalFile
        if (!file.toPath().startsWith(root.toPath()) || !file.isFile) {
            throw AppException(CodigoError.MED_001)
        }
        if (file.length() > 10L * 1024 * 1024) {
            throw AppException(CodigoError.MED_002)
        }
        val mediaType = (java.net.URLConnection.guessContentTypeFromName(file.name) ?: "image/jpeg").toMediaType()
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mediaType))
        val response = api.subirImagen(part).requireBody()
        if (!file.delete()) {
            throw AppException(CodigoError.MED_003)
        }
        return response.url
    }

    suspend fun uploadAll(paths: List<String>): List<String> =
        paths.mapNotNull { uploadIfLocal(it) }
}
