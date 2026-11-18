package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.OperationsApi
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toDto
import com.induztek.protocolos.model.Prueba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class PruebaRepository(private val api: OperationsApi) {
    private val _allPruebas = MutableStateFlow<List<Prueba>>(emptyList())
    val allPruebas: StateFlow<List<Prueba>> = _allPruebas.asStateFlow()

    suspend fun refresh() {
        _allPruebas.value = api.getPruebasByProtocolo(null).requireBody().map { it.toDomain() }
    }

    fun getPruebasByEquipo(equipoCodigo: String) = allPruebas.map { pruebas ->
        pruebas.filter { it.equipoCodigo == equipoCodigo }
    }

    fun getPruebasByProtocolo(protocoloId: String) = allPruebas.map { pruebas ->
        pruebas.filter { it.protocoloId == protocoloId }
    }

    suspend fun getPruebaById(id: String): Prueba? =
        api.getPrueba(id).let { response ->
            if (response.code() == 404) null else response.requireBody().toDomain()
        }

    suspend fun getUltimaPruebaPorTipo(equipoCodigo: String, tipoPrueba: String): Prueba? =
        api.getPruebasByEquipo(equipoCodigo).requireBody()
            .filter { it.tipoPrueba == tipoPrueba }
            .maxByOrNull { it.fechaHora }
            ?.toDomain()

    suspend fun insertPrueba(prueba: Prueba) {
        api.crearPrueba(prueba.toDto()).requireBody()
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
