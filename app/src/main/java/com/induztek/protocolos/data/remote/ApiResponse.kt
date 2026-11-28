package com.induztek.protocolos.data.remote

import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import retrofit2.Response

/** Devuelve el cuerpo; lanza HttpException si el código no es 2xx, o RED-003 si vino vacío. */
fun <T : Any> Response<T>.requireBody(): T {
    if (!isSuccessful) throw retrofit2.HttpException(this)
    return body() ?: throw AppException(CodigoError.RED_003, code())
}

/** Exige un código 2xx (para respuestas sin cuerpo, como 204); si no, lanza HttpException. */
fun Response<Unit>.requireSuccess() {
    if (!isSuccessful) throw retrofit2.HttpException(this)
}
