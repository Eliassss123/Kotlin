// ============================================================================
// ARCHIVO : data/remote/ApiService.kt
// CAPA    : Datos remotos (Retrofit)
// RESUMEN : describe las llamadas HTTP a un servidor (login, equipos, protocolos). Retrofit las convierte en
//           funciones Kotlin.
//           Hoy NO se usa: la app funciona solo con la base local. Queda preparado para conectar un backend en el
//             futuro.
// ============================================================================

package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Cada función es un 'endpoint' (una URL del servidor).
interface ApiService {

    // @POST = petición POST a esa ruta. @Body = el objeto se envía como JSON en el cuerpo. Response<...> = respuesta
    //   con código HTTP y datos.
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // @GET = petición GET (solo lee).
    @GET("api/equipos")
    suspend fun getEquipos(): Response<List<EquipoDto>>

    // {codigo} es un parámetro dentro de la URL; @Path lo reemplaza con el valor de la función.
    @GET("api/equipos/{codigo}/pruebas")
    suspend fun getPruebasByEquipo(@Path("codigo") codigo: String): Response<List<PruebaDto>>

    @GET("api/protocolos")
    suspend fun getProtocolos(): Response<List<ProtocoloDto>>

    @POST("api/protocolos")
    suspend fun syncProtocolo(@Body protocolo: ProtocoloDto): Response<ProtocoloDto>

    // Aquí se arma el cliente Retrofit real.
    companion object {
        // Dirección base del servidor (const = constante fija).
        private const val BASE_URL = "https://api.induztek.cl/"

        // Crea el cliente: baseUrl + convertidor Gson (JSON <-> objetos Kotlin).
        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
