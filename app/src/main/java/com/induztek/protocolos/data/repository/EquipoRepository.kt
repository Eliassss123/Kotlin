package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.CatalogApi
import com.induztek.protocolos.data.remote.requireBody
import com.induztek.protocolos.data.remote.toDomain
import com.induztek.protocolos.data.remote.toRequest
import com.induztek.protocolos.model.Equipo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class EquipoRepository(private val api: CatalogApi) {
    private val _allEquipos = MutableStateFlow<List<Equipo>>(emptyList())
    val allEquipos: StateFlow<List<Equipo>> = _allEquipos.asStateFlow()

    suspend fun refresh(cliente: String? = null) {
        _allEquipos.value = api.getEquipos(cliente).requireBody().map { it.toDomain() }
    }

    fun getEquiposByCliente(cliente: String) = allEquipos.map { equipos ->
        equipos.filter { it.cliente.equals(cliente, ignoreCase = true) }
    }

    suspend fun getEquipoByCodigo(codigo: String): Equipo? =
        api.getEquipoByCodigo(codigo).let { response ->
            if (response.code() == 404) null else response.requireBody().toDomain()
        }

    suspend fun getEquipoById(id: String): Equipo? {
        val cached = _allEquipos.value.firstOrNull { it.id == id }
        return cached ?: run {
            refresh()
            _allEquipos.value.firstOrNull { it.id == id }
        }
    }

    suspend fun buscarPosibleDuplicado(
        ubicacion: String,
        tag: String,
        numeroSerie: String,
        codigo: String
    ): Equipo? {
        val result = api.buscarDuplicado(ubicacion, tag, numeroSerie, codigo).requireBody()
        return result.equipment?.let { api.getEquipoByCodigo(it.codigo).requireBody().toDomain() }
    }

    suspend fun insertEquipo(equipo: Equipo) {
        val exists = _allEquipos.value.any { it.codigo == equipo.codigo }
        if (exists) {
            api.actualizarEquipo(equipo.codigo, equipo.toRequest()).requireBody()
        } else {
            api.crearEquipo(equipo.toRequest()).requireBody()
        }
        refresh()
    }

    suspend fun seedInitialDataIfEmpty() = refresh()
}
