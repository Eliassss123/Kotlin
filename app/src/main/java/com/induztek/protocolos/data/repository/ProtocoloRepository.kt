package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.FormalizeRequest
import com.induztek.protocolos.data.remote.OperationsApi
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toDto
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Protocolo
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ProtocoloRepository(
    private val api: OperationsApi,
    private val mediaRepository: MediaRepository
) {
    private val _allProtocolos = MutableStateFlow<List<Protocolo>>(emptyList())
    val allProtocolos: StateFlow<List<Protocolo>> = _allProtocolos.asStateFlow()

    suspend fun refresh() {
        _allProtocolos.value = api.getProtocolos().requireBody().map { it.toDomain() }
    }

    fun getProtocolosByVisita(visitaId: String) = allProtocolos.map { protocols ->
        protocols.filter { it.visitaId == visitaId }
    }

    fun getProtocolosByEquipo(equipoCodigo: String) = allProtocolos.map { protocols ->
        protocols.filter { it.equipoCodigo == equipoCodigo }
    }

    suspend fun getProtocoloById(id: String): Protocolo? =
        api.getProtocolo(id).let { response ->
            if (response.code() == 404) null else response.requireBody().toDomain()
        }

    suspend fun existeProtocoloParaEquipoEnVisita(visitaId: String, equipoCodigo: String): Boolean =
        api.getProtocolos(visitaId = visitaId, equipoCodigo = equipoCodigo).requireBody().isNotEmpty()

    suspend fun insertProtocolo(protocolo: Protocolo) {
        val uploadedPhotos = mediaRepository.uploadAll(protocolo.fotos)
        api.crearProtocolo(protocolo.copy(fotos = uploadedPhotos).toDto()).requireBody()
        refresh()
    }

    suspend fun formalizarProtocolo(
        id: String,
        usuarioFormalizador: String,
        nuevoEstado: EstadoProtocolo
    ) {
        require(nuevoEstado == EstadoProtocolo.FORMALIZADO) {
            "The backend only supports the formalized protocol transition"
        }
        api.formalizarProtocolo(id, FormalizeRequest(usuarioFormalizador)).requireBody()
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
