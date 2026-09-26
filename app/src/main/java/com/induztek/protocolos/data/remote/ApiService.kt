package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/equipos")
    suspend fun getEquipos(): Response<List<EquipoDto>>

    @GET("api/equipos/{codigo}/pruebas")
    suspend fun getPruebasByEquipo(@Path("codigo") codigo: String): Response<List<PruebaDto>>

    @GET("api/protocolos")
    suspend fun getProtocolos(): Response<List<ProtocoloDto>>

    @POST("api/protocolos")
    suspend fun syncProtocolo(@Body protocolo: ProtocoloDto): Response<ProtocoloDto>

    companion object {
        private const val BASE_URL = "https://api.induztek.cl/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
