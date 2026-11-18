package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.CatalogApi
import com.induztek.protocolos.data.remote.CalibrationRequest
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toDto
import com.induztek.protocolos.model.Instrumento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class InstrumentoRepository(private val api: CatalogApi) {
    private val _allInstrumentos = MutableStateFlow<List<Instrumento>>(emptyList())
    val allInstrumentos: StateFlow<List<Instrumento>> = _allInstrumentos.asStateFlow()
    val instrumentosActivos = allInstrumentos.map { instruments -> instruments.filter(Instrumento::activo) }

    suspend fun refresh() {
        _allInstrumentos.value = api.getInstrumentos().requireBody().map { it.toDomain() }
    }

    suspend fun getInstrumentoById(id: String): Instrumento? {
        val cached = _allInstrumentos.value.firstOrNull { it.id == id }
        return cached ?: run {
            refresh()
            _allInstrumentos.value.firstOrNull { it.id == id }
        }
    }

    suspend fun insertInstrumento(instrumento: Instrumento) {
        api.crearInstrumento(instrumento.toDto()).requireBody()
        refresh()
    }

    suspend fun actualizarCalibracion(id: String, nuevoVencimiento: String) {
        api.actualizarCalibracion(id, CalibrationRequest(nuevoVencimiento)).requireBody()
        refresh()
    }

    suspend fun setActivo(id: String, activo: Boolean) {
        api.actualizarEstadoInstrumento(
            id,
            com.induztek.protocolos.data.remote.InstrumentActiveRequest(activo)
        ).requireBody()
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
