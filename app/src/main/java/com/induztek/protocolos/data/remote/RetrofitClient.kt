package com.induztek.protocolos.data.remote

import com.induztek.protocolos.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL.ensureTrailingSlash()

    private var accessTokenProvider: () -> String? = { null }

    /**
     * Segundos que la app espera la respuesta. Debe superar el timeout de identity-service hacia
     * notifications-service (50 s): con el valor por defecto de OkHttp (10 s) la app cortaba la
     * solicitud del código de recuperación antes de que el backend terminara de enviar el correo.
     */
    const val READ_TIMEOUT_SECONDS = 60L
    const val CONNECT_TIMEOUT_SECONDS = 15L

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val token = accessTokenProvider()
                val requestBuilder = request.newBuilder()
                    .header("X-Tunnel-Skip-Anti-Phishing-Page", "true")
                if (!token.isNullOrBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
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
