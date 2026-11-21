package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface RolesApi {
    @GET("api/roles")
    suspend fun getRoles(): Response<List<RoleDto>>
}
