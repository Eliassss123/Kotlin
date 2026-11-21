package com.induztek.protocolos.data.remote

import java.io.IOException
import retrofit2.Response

fun <T : Any> Response<T>.requireBody(): T {
    if (!isSuccessful) throw retrofit2.HttpException(this)
    return body() ?: throw IOException("The server returned an empty response for HTTP ${code()}")
}

fun Response<Unit>.requireSuccess() {
    if (!isSuccessful) throw retrofit2.HttpException(this)
}
