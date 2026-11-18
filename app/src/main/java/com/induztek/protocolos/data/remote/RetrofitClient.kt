package com.induztek.protocolos.data.remote

import com.induztek.protocolos.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.ensureTrailingSlash()

    private var accessTokenProvider: () -> String? = { null }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val token = accessTokenProvider()
                val authenticatedRequest = if (token.isNullOrBlank()) {
                    request
                } else {
                    request.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
                chain.proceed(authenticatedRequest)
            })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val rolesApi: RolesApi by lazy { retrofit.create(RolesApi::class.java) }
    val catalogApi: CatalogApi by lazy { retrofit.create(CatalogApi::class.java) }
    val operationsApi: OperationsApi by lazy { retrofit.create(OperationsApi::class.java) }
    val mediaApi: MediaApi by lazy { retrofit.create(MediaApi::class.java) }

    fun configureAccessTokenProvider(provider: () -> String?) {
        accessTokenProvider = provider
    }

    private fun String.ensureTrailingSlash(): String =
        if (endsWith('/')) this else "$this/"
}
