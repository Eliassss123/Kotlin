package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.OperationsApi
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toDto
import com.induztek.protocolos.model.Acta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActaRepository(private val api: OperationsApi) {
    private val _allActas = MutableStateFlow<List<Acta>>(emptyList())
    val allActas: StateFlow<List<Acta>> = _allActas.asStateFlow()

    suspend fun refresh() {
        _allActas.value = api.getActas().requireBody().map { it.toDomain() }
    }

    suspend fun getActaById(id: String): Acta? =
        api.getActa(id).let { response ->
            if (response.code() == 404) null else response.requireBody().toDomain()
        }

    suspend fun getActaByVisita(visitaId: String): Acta? =
        api.getActas(visitaId).requireBody().firstOrNull()?.toDomain()

    suspend fun insertActa(acta: Acta) {
        api.crearActa(acta.toDto()).requireBody()
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
