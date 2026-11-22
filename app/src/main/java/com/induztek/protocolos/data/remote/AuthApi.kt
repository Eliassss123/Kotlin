package com.induztek.protocolos.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponseDto>

    @POST("api/auth/password/recovery")
    suspend fun requestPasswordRecovery(@Body request: RecoveryRequest): Response<MessageDto>

    @POST("api/auth/password/verify")
    suspend fun verifyRecoveryCode(@Body request: VerifyResetCodeRequest): Response<Unit>

    @POST("api/auth/password/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageDto>

    @GET("api/usuarios/me")
    suspend fun getCurrentUser(): Response<UserResponseDto>

    @PATCH("api/usuarios/me/perfil")
    suspend fun updateProfile(@Body request: UserProfileUpdateRequest): Response<UserResponseDto>

    @GET("api/usuarios")
    suspend fun getUsers(): Response<List<UserResponseDto>>

    @PATCH("api/usuarios/{id}/estado")
    suspend fun setUserActive(@Path("id") id: String, @Body request: ActiveRequest): Response<UserResponseDto>

    @PATCH("api/usuarios/{id}/rol")
    suspend fun setUserRole(@Path("id") id: String, @Body request: RoleRequest): Response<UserResponseDto>

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>
}
