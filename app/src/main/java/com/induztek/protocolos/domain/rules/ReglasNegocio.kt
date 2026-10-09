// ============================================================================
// ARCHIVO : domain/rules/ReglasNegocio.kt
// CAPA    : Dominio / Reglas de Negocio
// RESUMEN : Implementación centralizada de las Reglas de Negocio (RN-01 a RN-22)
//           y requisitos técnicos (RT-01 a RT-11) según síntesis del 08-10-2026.
// ============================================================================

package com.induztek.protocolos.domain.rules

import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.TipoEquipo
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

object ReglasNegocio {

    // ========================================================================
    // RN-05: Pruebas obligatorias por tipo de equipo (No agregar otras como FA-06)
    // ========================================================================
    fun obtenerPruebasRequeridasPorTipo(tipoEquipoStr: String): List<String> {
        val tipo = TipoEquipo.fromNombre(tipoEquipoStr)
        return tipo.pruebasRequeridas
    }

    // ========================================================================
    // RT-05, RN-06: Cálculo de Índice de Polarización (IP)
    // IP = lectura 10 min / lectura 1 min (con 2 decimales fijos)
    // Criterios: IP >= 2.0 (Aprobado), 1.5 <= IP < 2.0 (Observado), < 1.5 (Rechazado)
    // ========================================================================
    fun calcularIndicePolarizacion(lectura1min: Double, lectura10min: Double): Pair<Double, ResultadoPrueba> {
        if (lectura1min <= 0.0) return 0.0 to ResultadoPrueba.RECHAZADO
        val ipCalculado = lectura10min / lectura1min
        val ipRedondeado = Math.round(ipCalculado * 100.0) / 100.0

        val resultado = when {
            ipRedondeado >= 2.0 -> ResultadoPrueba.APROBADO
            ipRedondeado >= 1.5 -> ResultadoPrueba.OBSERVADO
            else -> ResultadoPrueba.RECHAZADO
        }
        return ipRedondeado to resultado
    }

    // ========================================================================
    // RT-05, RN-07: Error % de Relación de Transformación (TTR)
    // Error % = ((medida - teórica) / teórica) * 100
    // Criterios: <= 0.5% (Aprobado), 0.5% < Error <= 1.0% (Observado), > 1.0% (Rechazado)
    // ========================================================================
    fun calcularErrorTTR(relacionTeorica: Double, relacionMedida: Double): Pair<Double, ResultadoPrueba> {
        if (relacionTeorica <= 0.0) return 0.0 to ResultadoPrueba.RECHAZADO
        val errorPorcentaje = abs((relacionMedida - relacionTeorica) / relacionTeorica) * 100.0
        val errorRedondeado = Math.round(errorPorcentaje * 100.0) / 100.0

        val resultado = when {
            errorRedondeado <= 0.50 -> ResultadoPrueba.APROBADO
            errorRedondeado <= 1.00 -> ResultadoPrueba.OBSERVADO
            else -> ResultadoPrueba.RECHAZADO
        }
        return errorRedondeado to resultado
    }

    // ========================================================================
    // RT-05, RN-08: Resistencia de Contactos (Interruptores y Celdas MT)
    // Criterios: <= 50 µΩ (Aprobado), 50 < R <= 100 µΩ (Observado), > 100 µΩ (Rechazado)
    // ========================================================================
    fun evaluarResistenciaContactos(resistenciaMicroOhm: Double): ResultadoPrueba {
        return when {
            resistenciaMicroOhm <= 50.0 -> ResultadoPrueba.APROBADO
            resistenciaMicroOhm <= 100.0 -> ResultadoPrueba.OBSERVADO
            else -> ResultadoPrueba.RECHAZADO
        }
    }

    // ========================================================================
    // RT-05, RN-08b: Prueba de Operación Relé de Protección (Inyección secundaria)
    // Criterios error tiempo de disparo: <= 5.0% (Aprobado), 5.0% < Error <= 10.0% (Observado), > 10.0% (Rechazado)
    // ========================================================================
    fun evaluarOperacionRele(tiempoTeoricoSeg: Double, tiempoMedidoSeg: Double): Pair<Double, ResultadoPrueba> {
        if (tiempoTeoricoSeg <= 0.0) return 0.0 to ResultadoPrueba.RECHAZADO
        val errorPorcentaje = abs((tiempoMedidoSeg - tiempoTeoricoSeg) / tiempoTeoricoSeg) * 100.0
        val errorRedondeado = Math.round(errorPorcentaje * 100.0) / 100.0

        val resultado = when {
            errorRedondeado <= 5.0 -> ResultadoPrueba.APROBADO
            errorRedondeado <= 10.0 -> ResultadoPrueba.OBSERVADO
            else -> ResultadoPrueba.RECHAZADO
        }
        return errorRedondeado to resultado
    }

    // ========================================================================
    // RN-08c: Resistencia de Devanados (Sin cálculo automático)
    // El ingeniero asigna el resultado a mano
    // ========================================================================
    fun esPruebaConAsignacionManual(tipoPrueba: String): Boolean {
        return tipoPrueba.contains("devanado", ignoreCase = true)
    }

    // ========================================================================
    // RN-09, RT-06: Variación histórica contra la última prueba del mismo tipo
    // variacion = ((actual - anterior) / anterior) * 100. Si abs(variacion) > 25%, se destaca.
    // ========================================================================
    fun calcularVariacionHistorica(valorActual: Double, valorAnterior: Double): Pair<Double, Boolean> {
        if (valorAnterior == 0.0) return 0.0 to false
        val variacion = ((valorActual - valorAnterior) / valorAnterior) * 100.0
        val variacionRedondeada = Math.round(variacion * 10.0) / 10.0
        val supera25 = abs(variacion) > 25.0
        return variacionRedondeada to supera25
    }

    // ========================================================================
    // RN-10: Resultado Global del Protocolo
    // peor(resultados) con orden APROBADO < OBSERVADO < RECHAZADO.
    // Se excluyen las pruebas NO_REALIZADA.
    // ========================================================================
    fun calcularResultadoGlobal(pruebas: List<Prueba>): ResultadoPrueba {
        val pruebasRealizadas = pruebas.filter { it.estadoPrueba == EstadoPrueba.REALIZADA }
        if (pruebasRealizadas.isEmpty()) return ResultadoPrueba.NO_REALIZADA
        return ResultadoPrueba.peorResultado(pruebasRealizadas.map { it.resultado })
    }

    // ========================================================================
    // Aclaración 2, RT-08: Validación de Pruebas
    // Toda prueba debe quedar REALIZADA o NO_REALIZADA con motivo no vacío.
    // ========================================================================
    fun validarPruebaCompleta(prueba: Prueba): String? {
        if (prueba.estadoPrueba == EstadoPrueba.NO_REALIZADA) {
            if (prueba.motivoNoRealizada.isBlank()) {
                return "La prueba '${prueba.tipoPrueba}' está marcada como NO REALIZADA y requiere un motivo no vacío."
            }
        } else {
            if (prueba.mediciones.isEmpty()) {
                return "La prueba '${prueba.tipoPrueba}' debe tener al menos una medición registrada o marcarse como NO REALIZADA con su motivo."
            }
        }
        return null
    }

    // ========================================================================
    // Aclaración 4: Selección de mediciones en modalidad PRUEBAS_Y_MANTENIMIENTO
    // Si hay medición POSTERIOR, vale la posterior para el cálculo del resultado.
    // ========================================================================
    fun filtrarMedicionVigente(mediciones: List<Medicion>, campo: String): Medicion? {
        val posteriores = mediciones.filter { it.campo.equals(campo, ignoreCase = true) && it.momento == MomentoMedicion.POSTERIOR_MANTENIMIENTO }
        if (posteriores.isNotEmpty()) return posteriores.last()
        return mediciones.find { it.campo.equals(campo, ignoreCase = true) }
    }

    // ========================================================================
    // RN-02: Formato de número de protocolo único (PT-2026-0401)
    // ========================================================================
    fun generarNumeroProtocolo(correlativo: Int, anio: Int = 2026): String {
        return String.format(Locale.US, "PT-%d-%04d", anio, correlativo)
    }

    // ========================================================================
    // Plantillas de mediciones con campos según Anexo 12 (FT-03)
    // ========================================================================
    fun generarMedicionesPlantillaPorPrueba(tipoPrueba: String): List<Medicion> {
        return when {
            tipoPrueba.contains("aislación", ignoreCase = true) || tipoPrueba.contains("aislamiento", ignoreCase = true) -> listOf(
                Medicion(campo = "Tensión de Prueba", valorNumerico = 2500.0, unidad = "V"),
                Medicion(campo = "Lectura 1 min (R1min)", valorNumerico = 4.5, unidad = "GΩ"),
                Medicion(campo = "Lectura 10 min (R10min)", valorNumerico = 8.5, unidad = "GΩ"),
                Medicion(campo = "Índice de Polarización (IP)", valorNumerico = 1.89, unidad = "ratio")
            )
            tipoPrueba.contains("TTR", ignoreCase = true) || tipoPrueba.contains("transformación", ignoreCase = true) -> listOf(
                Medicion(campo = "Relación Teórica (Placa)", valorNumerico = 21.74, unidad = "ratio"),
                Medicion(campo = "Relación Medida (Fase U)", valorNumerico = 21.76, unidad = "ratio"),
                Medicion(campo = "Relación Medida (Fase V)", valorNumerico = 21.75, unidad = "ratio"),
                Medicion(campo = "Relación Medida (Fase W)", valorNumerico = 21.73, unidad = "ratio"),
                Medicion(campo = "Error Máximo %", valorNumerico = 0.09, unidad = "%")
            )
            tipoPrueba.contains("devanados", ignoreCase = true) -> listOf(
                Medicion(campo = "Devanado AT Fase U-V", valorNumerico = 1.25, unidad = "Ω"),
                Medicion(campo = "Devanado AT Fase V-W", valorNumerico = 1.26, unidad = "Ω"),
                Medicion(campo = "Devanado AT Fase W-U", valorNumerico = 1.24, unidad = "Ω"),
                Medicion(campo = "Devanado BT Fase u-v", valorNumerico = 12.4, unidad = "mΩ"),
                Medicion(campo = "Devanado BT Fase v-w", valorNumerico = 12.3, unidad = "mΩ"),
                Medicion(campo = "Devanado BT Fase w-u", valorNumerico = 12.5, unidad = "mΩ")
            )
            tipoPrueba.contains("contactos", ignoreCase = true) -> listOf(
                Medicion(campo = "Resistencia Polo / Fase R", valorNumerico = 42.0, unidad = "µΩ"),
                Medicion(campo = "Resistencia Polo / Fase S", valorNumerico = 45.0, unidad = "µΩ"),
                Medicion(campo = "Resistencia Polo / Fase T", valorNumerico = 40.0, unidad = "µΩ")
            )
            tipoPrueba.contains("fase", ignoreCase = true) -> listOf(
                Medicion(campo = "Tensión Aplicada Fase R", valorNumerico = 5000.0, unidad = "V"),
                Medicion(campo = "Lectura 1 min Fase R", valorNumerico = 6.2, unidad = "GΩ"),
                Medicion(campo = "Lectura 10 min Fase R", valorNumerico = 12.8, unidad = "GΩ"),
                Medicion(campo = "IP Fase R", valorNumerico = 2.06, unidad = "ratio")
            )
            tipoPrueba.contains("operación", ignoreCase = true) || tipoPrueba.contains("inyección", ignoreCase = true) -> listOf(
                Medicion(campo = "Corriente Inyectada (I_inj)", valorNumerico = 5.0, unidad = "A"),
                Medicion(campo = "Tiempo Teórico de Curva", valorNumerico = 0.40, unidad = "s"),
                Medicion(campo = "Tiempo Medido de Disparo", valorNumerico = 0.41, unidad = "s"),
                Medicion(campo = "Error Tiempo Disparo %", valorNumerico = 2.5, unidad = "%")
            )
            else -> listOf(
                Medicion(campo = "Parámetro Principal", valorNumerico = 1.0, unidad = "")
            )
        }
    }
}
