






package com.induztek.protocolos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.induztek.protocolos.data.repository.EquipoRepository
import com.induztek.protocolos.data.repository.PruebaRepository
import com.induztek.protocolos.model.ClasificacionEquipo
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.TipoEquipo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID


data class NuevoEquipoState(
    val codigo: String = "",
    val tagCliente: String = "",
    val tipo: String = TipoEquipo.TRANSFORMADOR.nombre,
    val cliente: String = "Planta Lampa",
    val ubicacion: String = "Subestación Principal Norte",
    val marca: String = "",
    val modelo: String = "",
    val numeroSerie: String = "",
    val potenciaTension: String = "",
    val anio: String = "2024",
    val tensiones: String = "23 kV / 380 V",
    val tap: String = "3",
    val conexion: String = "Dyn11",
    val conexionOtro: String = "",
    val errorValidacion: String? = null,
    val advertenciaDuplicado: String? = null
)


class EquipoViewModel(
    private val equipoRepository: EquipoRepository,
    private val pruebaRepository: PruebaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filtroClasificacion = MutableStateFlow<ClasificacionEquipo?>(null)
    val filtroClasificacion: StateFlow<ClasificacionEquipo?> = _filtroClasificacion.asStateFlow()

    private val _empresaClienteFiltro = MutableStateFlow<String?>(null)
    val empresaClienteFiltro: StateFlow<String?> = _empresaClienteFiltro.asStateFlow()

    private val _nuevoEquipoState = MutableStateFlow(NuevoEquipoState())
    val nuevoEquipoState: StateFlow<NuevoEquipoState> = _nuevoEquipoState.asStateFlow()

    init {
        viewModelScope.launch {
            equipoRepository.seedInitialDataIfEmpty()
            pruebaRepository.seedInitialDataIfEmpty()
        }
    }

    
    val equipos: StateFlow<List<Equipo>> = combine(
        equipoRepository.allEquipos,
        _searchQuery,
        _filtroClasificacion,
        _empresaClienteFiltro
    ) { lista, query, clasificacion, empresaCliente ->
        var resultado = lista

        if (!empresaCliente.isNullOrBlank()) {
            resultado = resultado.filter { it.cliente.equals(empresaCliente, ignoreCase = true) }
        }

        if (clasificacion != null) {
            resultado = resultado.filter { it.clasificacion == clasificacion }
        }

        if (query.isNotBlank()) {
            resultado = resultado.filter {
                it.codigo.contains(query, ignoreCase = true) ||
                it.tagCliente.contains(query, ignoreCase = true) ||
                it.cliente.contains(query, ignoreCase = true) ||
                it.tipo.contains(query, ignoreCase = true) ||
                it.ubicacion.contains(query, ignoreCase = true) ||
                it.numeroSerie.contains(query, ignoreCase = true)
            }
        }
        resultado
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    
    fun setFiltroClasificacion(clasificacion: ClasificacionEquipo?) {
        _filtroClasificacion.value = clasificacion
    }

    
    fun setEmpresaCliente(empresa: String?) {
        _empresaClienteFiltro.value = empresa
    }

    private val _selectedEquipo = MutableStateFlow<Equipo?>(null)
    val selectedEquipo: StateFlow<Equipo?> = _selectedEquipo.asStateFlow()

    private val _historialPruebas = MutableStateFlow<List<Prueba>>(emptyList())
    val historialPruebas: StateFlow<List<Prueba>> = _historialPruebas.asStateFlow()

    
    fun cargarDetalleEquipo(codigo: String) {
        viewModelScope.launch {
            val equipo = equipoRepository.getEquipoByCodigo(codigo)
            _selectedEquipo.value = equipo
            if (equipo != null) {
                pruebaRepository.getPruebasByEquipo(equipo.codigo).collect { pruebas ->
                    _historialPruebas.value = pruebas
                }
            }
        }
    }

    
    fun onCodigoChanged(codigo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(codigo = codigo, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    
    fun onTagClienteChanged(tag: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tagCliente = tag, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    
    fun onTipoChanged(tipo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tipo = tipo)
    }

    
    fun onClienteNuevoChanged(cliente: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(cliente = cliente, errorValidacion = null)
    }

    
    fun onUbicacionNuevaChanged(ubicacion: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(ubicacion = ubicacion, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    
    fun onMarcaChanged(marca: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(marca = marca)
    }

    
    fun onModeloChanged(modelo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(modelo = modelo)
    }

    
    fun onNumeroSerieChanged(serie: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(numeroSerie = serie, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    
    fun onPotenciaTensionChanged(potencia: String) {
        val soloNumeros = potencia.filter { it.isDigit() }
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(potenciaTension = soloNumeros)
    }

    
    fun onAnioChanged(anio: String) {
        val soloNumeros = anio.filter { it.isDigit() }.take(4)
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(anio = soloNumeros, errorValidacion = null)
    }

    
    fun onTensionesChanged(tensiones: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tensiones = tensiones)
    }

    
    fun onTapChanged(tap: String) {
        val soloNumeros = tap.filter { it.isDigit() }.take(2)
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tap = soloNumeros)
    }

    
    fun onConexionChanged(conexion: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(conexion = conexion)
    }

    
    fun onConexionOtroChanged(otro: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(conexionOtro = otro)
    }

    
    private fun verificarPosibleDuplicado() {
        val state = _nuevoEquipoState.value
        if (state.ubicacion.isNotBlank() && (state.tagCliente.isNotBlank() || state.numeroSerie.isNotBlank() || state.codigo.isNotBlank())) {
            viewModelScope.launch {
                val duplicado = equipoRepository.buscarPosibleDuplicado(
                    ubicacion = state.ubicacion.trim(),
                    tag = state.tagCliente.trim(),
                    numeroSerie = state.numeroSerie.trim(),
                    codigo = state.codigo.trim()
                )
                if (duplicado != null) {
                    _nuevoEquipoState.value = _nuevoEquipoState.value.copy(
                        advertenciaDuplicado = "Posible duplicado detectado: Ya existe el equipo '${duplicado.codigo}' [TAG: ${duplicado.tagCliente}] en esta instalación (RT-02)."
                    )
                } else {
                    _nuevoEquipoState.value = _nuevoEquipoState.value.copy(advertenciaDuplicado = null)
                }
            }
        }
    }

    
    fun guardarEquipoNuevo(onSuccess: (Equipo) -> Unit) {
        val state = _nuevoEquipoState.value

        val codigoIngresado = state.codigo.trim()
        val tieneTag = codigoIngresado.isNotBlank()
        if (!tieneTag) {
            if (state.marca.isBlank() || state.numeroSerie.isBlank()) {
                _nuevoEquipoState.value = state.copy(
                    errorValidacion = "El código del TAG es obligatorio (ej: TR-500KVA-01, CAB-XLPE-05). Si el equipo no posee TAG, debe ingresar obligatoriamente Marca y N° de Serie para identificarlo."
                )
                return
            }
        }

        if (state.cliente.isBlank()) {
            _nuevoEquipoState.value = state.copy(errorValidacion = "El cliente es obligatorio")
            return
        }
        if (state.ubicacion.isBlank()) {
            _nuevoEquipoState.value = state.copy(errorValidacion = "La instalación/ubicación es obligatoria")
            return
        }

        val anioInt = state.anio.toIntOrNull()
        if (anioInt == null || anioInt !in 1900..2026) {
            _nuevoEquipoState.value = state.copy(
                errorValidacion = "El año ingresado '${state.anio}' no es válido. Debe ser un número de 4 dígitos entre 1900 y 2026."
            )
            return
        }

        val codigoFinal = if (tieneTag) {
            codigoIngresado.uppercase()
        } else {
            "EQ-${state.marca.trim().take(4).uppercase()}-${state.numeroSerie.trim().uppercase()}"
        }

        val tapGuardar = state.tap.filter { it.isDigit() }.ifBlank { "3" }
        val conexionFinal = if (state.conexion == "Otro" && state.conexionOtro.isNotBlank()) {
            state.conexionOtro.trim()
        } else {
            state.conexion.trim()
        }

        viewModelScope.launch {
            val nuevoEquipo = Equipo(
                id = "EQ-" + UUID.randomUUID().toString().take(8).uppercase(),
                codigo = codigoFinal,
                tagCliente = state.tagCliente.trim().uppercase(),
                tipo = state.tipo,
                cliente = state.cliente.trim(),
                ubicacion = state.ubicacion.trim(),
                marca = state.marca.trim(),
                modelo = state.modelo.trim(),
                numeroSerie = state.numeroSerie.trim(),
                potenciaTension = if (state.potenciaTension.isNotBlank()) "${state.potenciaTension.trim()} kVA" else "",
                anio = state.anio.trim(),
                tensiones = state.tensiones.trim(),
                tap = tapGuardar,
                conexion = conexionFinal,
                ultimoEstado = EstadoProtocolo.REGISTRADO_TERRENO,
                empeorando = false
            )

            equipoRepository.insertEquipo(nuevoEquipo)
            _nuevoEquipoState.value = NuevoEquipoState()
            onSuccess(nuevoEquipo)
        }
    }
}
