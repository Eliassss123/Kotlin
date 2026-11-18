package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.OperationsApi
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toDto
import com.induztek.protocolos.model.Visita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class VisitaRepository(private val api: OperationsApi) {
    private val _allVisitas = MutableStateFlow<List<Visita>>(emptyList())
    val allVisitas: StateFlow<List<Visita>> = _allVisitas.asStateFlow()
    val visitaActiva = allVisitas.map { visits -> visits.firstOrNull { !it.cerrada } }

    suspend fun refresh() {
        _allVisitas.value = api.getVisitas().requireBody().map { it.toDomain() }
    }

    suspend fun getVisitaById(id: String): Visita? =
        api.getVisita(id).let { response ->
            if (response.code() == 404) null else response.requireBody().toDomain()
        }

    suspend fun insertVisita(visita: Visita) {
        api.crearVisita(visita.toDto()).requireBody()
        refresh()
    }

    suspend fun cerrarVisita(id: String) {
        api.cerrarVisita(id).requireBody()
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
