package com.induztek.protocolos.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaApi {
    @Multipart
    @POST("api/imagenes")
    suspend fun subirImagen(@Part file: MultipartBody.Part): Response<ImageUploadResponse>
}
