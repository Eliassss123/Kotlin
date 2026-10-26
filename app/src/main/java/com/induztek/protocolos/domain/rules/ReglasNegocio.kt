






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

    
    
    
    fun obtenerPruebasRequeridasPorTipo(tipoEquipoStr: String): List<String> {
        val tipo = TipoEquipo.fromNombre(tipoEquipoStr)
        return tipo.pruebasRequeridas
    }

    
    
    
    
    
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

    
    
    
    
    fun evaluarResistenciaContactos(resistenciaMicroOhm: Double): ResultadoPrueba {
        return when {
            resistenciaMicroOhm <= 50.0 -> ResultadoPrueba.APROBADO
            resistenciaMicroOhm <= 100.0 -> ResultadoPrueba.OBSERVADO
            else -> ResultadoPrueba.RECHAZADO
        }
    }

    
    
    
    
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

    
    
    
    
    fun esPruebaConAsignacionManual(tipoPrueba: String): Boolean {
        return tipoPrueba.contains("devanado", ignoreCase = true)
    }

    
    
    
    
    fun calcularVariacionHistorica(valorActual: Double, valorAnterior: Double): Pair<Double, Boolean> {
        if (valorAnterior == 0.0) return 0.0 to false
        val variacion = ((valorActual - valorAnterior) / valorAnterior) * 100.0
        val variacionRedondeada = Math.round(variacion * 10.0) / 10.0
        val supera25 = abs(variacion) > 25.0
        return variacionRedondeada to supera25
    }

    
    
    
    
    
    fun calcularResultadoGlobal(pruebas: List<Prueba>): ResultadoPrueba {
        val pruebasRealizadas = pruebas.filter { it.estadoPrueba == EstadoPrueba.REALIZADA }
        if (pruebasRealizadas.isEmpty()) return ResultadoPrueba.NO_REALIZADA
        return ResultadoPrueba.peorResultado(pruebasRealizadas.map { it.resultado })
    }

    
    
    
    
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

    
    
    
    
    fun filtrarMedicionVigente(mediciones: List<Medicion>, campo: String): Medicion? {
        val posteriores = mediciones.filter { it.campo.equals(campo, ignoreCase = true) && it.momento == MomentoMedicion.POSTERIOR_MANTENIMIENTO }
        if (posteriores.isNotEmpty()) return posteriores.last()
        return mediciones.find { it.campo.equals(campo, ignoreCase = true) }
    }

    
    
    
    fun generarNumeroProtocolo(correlativo: Int, anio: Int = 2026): String {
        return String.format(Locale.US, "PT-%d-%04d", anio, correlativo)
    }

    
    
    
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
