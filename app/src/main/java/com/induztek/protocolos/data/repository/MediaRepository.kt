package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.MediaApi
import com.induztek.protocolos.data.remote.requireBody
import java.io.File
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class MediaRepository(
    private val api: MediaApi,
    private val appFilesDir: File
) {
    suspend fun uploadIfLocal(path: String?): String? {
        if (path.isNullOrBlank() || path.startsWith("https://") || path.startsWith("http://")) return path
        val file = File(path).canonicalFile
        val root = appFilesDir.canonicalFile
        if (!file.toPath().startsWith(root.toPath()) || !file.isFile) {
            throw IOException("The selected image is not in the app's private storage")
        }
        if (file.length() > 10L * 1024 * 1024) {
            throw IOException("Images cannot exceed 10 MB")
        }
        val mediaType = (java.net.URLConnection.guessContentTypeFromName(file.name) ?: "image/jpeg").toMediaType()
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mediaType))
        val response = api.subirImagen(part).requireBody()
        if (!file.delete()) {
            throw IOException("The image was uploaded, but the temporary local copy could not be removed")
        }
        return response.url
    }

    suspend fun uploadAll(paths: List<String>): List<String> =
        paths.mapNotNull { uploadIfLocal(it) }
}
