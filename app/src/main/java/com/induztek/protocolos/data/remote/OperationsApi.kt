package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OperationsApi {
    @GET("api/visitas")
    suspend fun getVisitas(): Response<List<VisitDto>>

    @GET("api/visitas/{id}")
    suspend fun getVisita(@Path("id") id: String): Response<VisitDto>

    @POST("api/visitas")
    suspend fun crearVisita(@Body visita: VisitDto): Response<VisitDto>

    @PATCH("api/visitas/{id}/cerrar")
    suspend fun cerrarVisita(@Path("id") id: String): Response<VisitDto>

    @GET("api/protocolos")
    suspend fun getProtocolos(
        @Query("visitaId") visitaId: String? = null,
        @Query("equipoCodigo") equipoCodigo: String? = null
    ): Response<List<ProtocolDto>>

    @GET("api/protocolos/{id}")
    suspend fun getProtocolo(@Path("id") id: String): Response<ProtocolDto>

    @POST("api/protocolos")
    suspend fun crearProtocolo(@Body protocolo: ProtocolDto): Response<ProtocolDto>

    @POST("api/protocolos/{id}/formalizar")
    suspend fun formalizarProtocolo(@Path("id") id: String, @Body request: FormalizeRequest): Response<ProtocolDto>

    @GET("api/equipos/{codigo}/pruebas")
    suspend fun getPruebasByEquipo(@Path("codigo") codigo: String): Response<List<TestDto>>

    @GET("api/pruebas")
    suspend fun getPruebasByProtocolo(@Query("protocoloId") protocoloId: String? = null): Response<List<TestDto>>

    @POST("api/pruebas")
    suspend fun crearPrueba(@Body prueba: TestDto): Response<TestDto>

    @GET("api/pruebas/{id}")
    suspend fun getPrueba(@Path("id") id: String): Response<TestDto>

    @GET("api/actas")
    suspend fun getActas(@Query("visitaId") visitaId: String? = null): Response<List<ActaDto>>

    @GET("api/actas/{id}")
    suspend fun getActa(@Path("id") id: String): Response<ActaDto>

    @POST("api/actas")
    suspend fun crearActa(@Body acta: ActaDto): Response<ActaDto>
}

data class FormalizeRequest(val usuario: String)
