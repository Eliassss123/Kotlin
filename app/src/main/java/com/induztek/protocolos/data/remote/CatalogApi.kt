package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogApi {
    @GET("api/equipos")
    suspend fun getEquipos(@Query("cliente") cliente: String? = null): Response<List<EquipoDto>>

    @GET("api/equipos/{codigo}")
    suspend fun getEquipoByCodigo(@Path("codigo") codigo: String): Response<EquipoDto>

    @GET("api/equipos/duplicados")
    suspend fun buscarDuplicado(
        @Query("ubicacion") ubicacion: String,
        @Query("tag") tag: String,
        @Query("numeroSerie") numeroSerie: String,
        @Query("codigo") codigo: String
    ): Response<DuplicateResponse>

    @POST("api/equipos")
    suspend fun crearEquipo(@Body request: EquipmentRequest): Response<EquipoDto>

    @PUT("api/equipos/{codigo}")
    suspend fun actualizarEquipo(
        @Path("codigo") codigo: String,
        @Body request: EquipmentRequest
    ): Response<EquipoDto>

    @GET("api/instrumentos")
    suspend fun getInstrumentos(@Query("activos") activos: Boolean = false): Response<List<InstrumentDto>>

    @POST("api/instrumentos")
    suspend fun crearInstrumento(@Body instrumento: InstrumentDto): Response<InstrumentDto>

    @PUT("api/instrumentos/{id}/calibracion")
    suspend fun actualizarCalibracion(
        @Path("id") id: String,
        @Body request: CalibrationRequest
    ): Response<InstrumentDto>

    @PUT("api/instrumentos/{id}/estado")
    suspend fun actualizarEstadoInstrumento(
        @Path("id") id: String,
        @Body request: InstrumentActiveRequest
    ): Response<InstrumentDto>
}

data class CalibrationRequest(val vencimientoCalibracion: String)
data class InstrumentActiveRequest(val activo: Boolean)
