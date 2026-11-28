package com.induztek.protocolos.testutil

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

/** Crea un HttpException como el que lanza Retrofit, con el cuerpo `ApiError` del backend. */
fun httpError(status: Int, code: String? = null, message: String = "error", path: String = "/api/test"): HttpException {
    val body = if (code == null) "" else
        """{"timestamp":"2026-11-23T21:30:00Z","status":$status,"error":"x","code":"$code","message":"$message","path":"$path","details":[]}"""
    return HttpException(Response.error<Any>(status, body.toResponseBody("application/json".toMediaType())))
}
