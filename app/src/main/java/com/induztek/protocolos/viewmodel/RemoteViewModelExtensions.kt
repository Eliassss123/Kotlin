package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.remote.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Lanza una corrutina en el viewModelScope y entrega a [onError] un mensaje legible si falla.
 * Los mensajes por código HTTP están documentados en [com.induztek.protocolos.data.remote.ApiErrors].
 */
internal fun ViewModel.launchCatching(
    onError: (String) -> Unit,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception !is CancellationException) onError(exception.toUserMessage())
    }
    return viewModelScope.launch(context = exceptionHandler, block = block)
}
