// ============================================================================
// ARCHIVO : viewmodel/EquipoViewModel.kt
// CAPA    : ViewModel (MVVM)
// RESUMEN : Gestión de equipos, datos de placa (FT-03 sec. 2), detección de duplicados en terreno (RT-02),
//           filtro por empresa para cliente (RT-10, P-04) e indicador de tendencia histórica (RT-07).
// ============================================================================

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

/**
 * Estado de la UI para el formulario de creación de un equipo nuevo en terreno.
 */
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

/**
 * ViewModel que gestiona la lista de equipos, filtrado reactivo, placa de características (FT-03),
 * detección de duplicados en terreno (RT-02) y restricciones para usuario cliente (P-04).
 */
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

    /**
     * Flujo reactivo combinado que filtra la lista de equipos según buscador de texto,
     * clasificación (Primario/Secundario) y empresa cliente (RT-10, P-04).
     */
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

    /** Actualiza el texto de búsqueda de equipos. */
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /** Establece el filtro de clasificación (Primario / Secundario). */
    fun setFiltroClasificacion(clasificacion: ClasificacionEquipo?) {
        _filtroClasificacion.value = clasificacion
    }

    /** Establece el filtro por empresa cliente (P-04). */
    fun setEmpresaCliente(empresa: String?) {
        _empresaClienteFiltro.value = empresa
    }

    private val _selectedEquipo = MutableStateFlow<Equipo?>(null)
    val selectedEquipo: StateFlow<Equipo?> = _selectedEquipo.asStateFlow()

    private val _historialPruebas = MutableStateFlow<List<Prueba>>(emptyList())
    val historialPruebas: StateFlow<List<Prueba>> = _historialPruebas.asStateFlow()

    /**
     * Carga el detalle de un equipo específico y su historial de pruebas asociadas.
     * @param codigo Código único del equipo.
     */
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

    /** Actualiza el código en el formulario de nuevo equipo. */
    fun onCodigoChanged(codigo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(codigo = codigo, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    /** Actualiza el tag del cliente en el nuevo equipo. */
    fun onTagClienteChanged(tag: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tagCliente = tag, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    /** Actualiza el tipo de equipo. */
    fun onTipoChanged(tipo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tipo = tipo)
    }

    /** Actualiza el cliente del nuevo equipo. */
    fun onClienteNuevoChanged(cliente: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(cliente = cliente, errorValidacion = null)
    }

    /** Actualiza la ubicación o instalación del nuevo equipo. */
    fun onUbicacionNuevaChanged(ubicacion: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(ubicacion = ubicacion, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    /** Actualiza la marca del equipo. */
    fun onMarcaChanged(marca: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(marca = marca)
    }

    /** Actualiza el modelo del equipo. */
    fun onModeloChanged(modelo: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(modelo = modelo)
    }

    /** Actualiza el número de serie del equipo. */
    fun onNumeroSerieChanged(serie: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(numeroSerie = serie, errorValidacion = null)
        verificarPosibleDuplicado()
    }

    /** Actualiza la potencia y tensión del equipo. */
    fun onPotenciaTensionChanged(potencia: String) {
        val soloNumeros = potencia.filter { it.isDigit() }
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(potenciaTension = soloNumeros)
    }

    /** Actualiza el año de fabricación del equipo. */
    fun onAnioChanged(anio: String) {
        val soloNumeros = anio.filter { it.isDigit() }.take(4)
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(anio = soloNumeros, errorValidacion = null)
    }

    /** Actualiza las tensiones nominales. */
    fun onTensionesChanged(tensiones: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tensiones = tensiones)
    }

    /** Actualiza el tap del equipo. */
    fun onTapChanged(tap: String) {
        val soloNumeros = tap.filter { it.isDigit() }.take(2)
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(tap = soloNumeros)
    }

    /** Actualiza el tipo de conexión. */
    fun onConexionChanged(conexion: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(conexion = conexion)
    }

    /** Actualiza el texto personalizado de conexión si se selecciona "Otro". */
    fun onConexionOtroChanged(otro: String) {
        _nuevoEquipoState.value = _nuevoEquipoState.value.copy(conexionOtro = otro)
    }

    /**
     * RT-02: Verifica si ya existe un equipo similar en la misma ubicación y emite advertencia si es duplicado.
     */
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

    /**
     * Valida y guarda un nuevo equipo en el repositorio de equipos.
     * @param onSuccess Callback que recibe el [Equipo] creado exitosamente.
     */
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
