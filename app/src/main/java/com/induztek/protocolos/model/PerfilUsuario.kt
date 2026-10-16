// ============================================================================
// ARCHIVO : model/PerfilUsuario.kt
// CAPA    : Modelo (dominio)
// RESUMEN : perfiles de usuario y matriz de permisos según Anexo 7 y Síntesis Técnica:
//           P-01: Técnico de terreno (Registrar equipos, protocolos, mediciones; cerrar visita; entregar acta)
//           P-02: Ingeniero de pruebas (Todo lo de P-01 + interpretar, observar y formalizar)
//           P-03: Jefe técnico (Formalizar + administrar instrumentos, calibraciones, cuentas cliente y personal)
//           P-04: Usuario cliente (Solo consulta de actas, protocolos e historial de su empresa)
// ============================================================================

package com.induztek.protocolos.model

enum class PerfilUsuario(
    val codigo: String,
    val label: String,
    val descripcion: String,
    val puedeRegistrarEquipos: Boolean,
    val puedeRegistrarProtocolos: Boolean,
    val puedeCerrarVisita: Boolean,
    val puedeFormalizar: Boolean,
    val puedeGestionarInstrumentos: Boolean,
    val puedeGestionarPersonal: Boolean,
    val esSoloConsulta: Boolean
) {
    TECNICO_TERRENO(
        codigo = "P-01",
        label = "Técnico de Terreno",
        descripcion = "Registra equipos, protocolos y mediciones; cierra visita y entrega acta",
        puedeRegistrarEquipos = true,
        puedeRegistrarProtocolos = true,
        puedeCerrarVisita = true,
        puedeFormalizar = false,
        puedeGestionarInstrumentos = false,
        puedeGestionarPersonal = false,
        esSoloConsulta = false
    ),
    INGENIERO_PRUEBAS(
        codigo = "P-02",
        label = "Ingeniero de Pruebas",
        descripcion = "Lo mismo que técnico, más interpretar, observar y formalizar protocolos",
        puedeRegistrarEquipos = true,
        puedeRegistrarProtocolos = true,
        puedeCerrarVisita = true,
        puedeFormalizar = true,
        puedeGestionarInstrumentos = false,
        puedeGestionarPersonal = false,
        esSoloConsulta = false
    ),
    JEFE_TECNICO(
        codigo = "P-03",
        label = "Jefe Técnico",
        descripcion = "Formaliza protocolos; administra instrumentos, calibraciones, clientes y personal",
        puedeRegistrarEquipos = true,
        puedeRegistrarProtocolos = true,
        puedeCerrarVisita = true,
        puedeFormalizar = true,
        puedeGestionarInstrumentos = true,
        puedeGestionarPersonal = true,
        esSoloConsulta = false
    ),
    CLIENTE(
        codigo = "P-04",
        label = "Usuario Cliente",
        descripcion = "Consulta actas, protocolos e historial de su empresa (solo lectura)",
        puedeRegistrarEquipos = false,
        puedeRegistrarProtocolos = false,
        puedeCerrarVisita = false,
        puedeFormalizar = false,
        puedeGestionarInstrumentos = false,
        puedeGestionarPersonal = false,
        esSoloConsulta = true
    );

    val puedeRegistrarPruebas: Boolean
        get() = puedeRegistrarProtocolos

    val esTrabajadorInduztek: Boolean
        get() = this != CLIENTE

    val tipoUsuario: String
        get() = if (esTrabajadorInduztek) "Personal Induztek" else "Cliente Externo"

    companion object {
        fun fromCodigo(codigo: String): PerfilUsuario {
            return entries.find {
                it.codigo.equals(codigo, ignoreCase = true) || it.name.equals(codigo, ignoreCase = true)
            } ?: TECNICO_TERRENO
        }

        fun fromLabel(label: String): PerfilUsuario {
            return entries.find { it.label.equals(label, ignoreCase = true) }
                ?: fromCodigo(label)
        }
    }
}
