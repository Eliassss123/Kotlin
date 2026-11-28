package com.induztek.protocolos.domain

import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.domain.rules.ReglasNegocio
import com.induztek.protocolos.model.EstadoPrueba
import com.induztek.protocolos.model.Medicion
import com.induztek.protocolos.model.MomentoMedicion
import com.induztek.protocolos.model.Prueba
import com.induztek.protocolos.model.ResultadoPrueba
import com.induztek.protocolos.model.TipoEquipo
import com.induztek.protocolos.testutil.assertCodigo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reglas de negocio de las pruebas eléctricas (ReglasNegocio).
 *
 * Criterios de aceptación:
 * - CA-REG-01: Índice de Polarización IP = R10/R1; ≥ 2.0 aprobado, ≥ 1.5 observado, < 1.5 o R1 ≤ 0 rechazado.
 * - CA-REG-02: Error TTR ≤ 0.5 % aprobado, ≤ 1 % observado, mayor o relación teórica ≤ 0 rechazado.
 * - CA-REG-03: Resistencia de contactos ≤ 50 µΩ aprobado, ≤ 100 µΩ observado, mayor rechazado.
 * - CA-REG-04: Error de tiempo de relé ≤ 5 % aprobado, ≤ 10 % observado, mayor rechazado.
 * - CA-REG-05: Variación histórica > 25 % se marca; con valor anterior 0 no se calcula.
 * - CA-REG-06: El resultado global es el peor de las pruebas realizadas; sin pruebas realizadas es NO_REALIZADA.
 * - CA-REG-07: Una prueba NO REALIZADA requiere motivo (REG-001); una REALIZADA requiere mediciones (REG-002).
 * - CA-REG-08: Se usa la medición POSTERIOR al mantenimiento si existe; si no, la previa.
 * - CA-REG-09: Cada tipo de equipo genera sus pruebas requeridas y cada prueba su plantilla de mediciones.
 * - CA-REG-10: El número de protocolo sigue el formato PT-AAAA-NNNN.
 */
class ReglasNegocioTest {

    private fun prueba(estado: EstadoPrueba, motivo: String = "", mediciones: List<Medicion> = emptyList(),
                       resultado: ResultadoPrueba = ResultadoPrueba.APROBADO) =
        Prueba(tipoPrueba = "Resistencia de aislación", estadoPrueba = estado, motivoNoRealizada = motivo,
            mediciones = mediciones, resultado = resultado)

    // CA-REG-01: IP 10/4 = 2.5 aprueba.
    @Test
    fun ipMayorOIgualA2Aprueba() {
        assertEquals(2.5 to ResultadoPrueba.APROBADO, ReglasNegocio.calcularIndicePolarizacion(4.0, 10.0))
    }

    // CA-REG-01: IP entre 1.5 y 2.0 queda observado.
    @Test
    fun ipEntre15Y2QuedaObservado() {
        assertEquals(1.8 to ResultadoPrueba.OBSERVADO, ReglasNegocio.calcularIndicePolarizacion(5.0, 9.0))
    }

    // CA-REG-01: IP bajo 1.5 o lectura de 1 min inválida se rechaza.
    @Test
    fun ipBajoOLecturaInvalidaSeRechaza() {
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.calcularIndicePolarizacion(5.0, 6.0).second)
        assertEquals(0.0 to ResultadoPrueba.RECHAZADO, ReglasNegocio.calcularIndicePolarizacion(0.0, 6.0))
    }

    // CA-REG-02: los tres rangos del error de relación TTR.
    @Test
    fun errorTtrPorRangos() {
        assertEquals(ResultadoPrueba.APROBADO, ReglasNegocio.calcularErrorTTR(20.0, 20.05).second)
        assertEquals(ResultadoPrueba.OBSERVADO, ReglasNegocio.calcularErrorTTR(20.0, 20.15).second)
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.calcularErrorTTR(20.0, 20.5).second)
        assertEquals(0.0 to ResultadoPrueba.RECHAZADO, ReglasNegocio.calcularErrorTTR(0.0, 20.0))
    }

    // CA-REG-03: límites de 50 y 100 µΩ en resistencia de contactos.
    @Test
    fun resistenciaContactosPorRangos() {
        assertEquals(ResultadoPrueba.APROBADO, ReglasNegocio.evaluarResistenciaContactos(50.0))
        assertEquals(ResultadoPrueba.OBSERVADO, ReglasNegocio.evaluarResistenciaContactos(100.0))
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.evaluarResistenciaContactos(100.1))
    }

    // CA-REG-04: error de tiempo de disparo del relé por rangos.
    @Test
    fun operacionRelePorRangos() {
        assertEquals(ResultadoPrueba.APROBADO, ReglasNegocio.evaluarOperacionRele(0.40, 0.41).second)
        assertEquals(ResultadoPrueba.OBSERVADO, ReglasNegocio.evaluarOperacionRele(0.40, 0.43).second)
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.evaluarOperacionRele(0.40, 0.50).second)
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.evaluarOperacionRele(0.0, 0.50).second)
    }

    // CA-REG-05: una variación de +30 % supera el umbral del 25 %.
    @Test
    fun variacionHistoricaSobre25SeMarca() {
        assertEquals(30.0 to true, ReglasNegocio.calcularVariacionHistorica(13.0, 10.0))
        assertEquals(10.0 to false, ReglasNegocio.calcularVariacionHistorica(11.0, 10.0))
        assertEquals(0.0 to false, ReglasNegocio.calcularVariacionHistorica(11.0, 0.0))
    }

    // CA-REG-06: el resultado global es el peor e ignora las no realizadas.
    @Test
    fun resultadoGlobalEsElPeor() {
        val pruebas = listOf(
            prueba(EstadoPrueba.REALIZADA, resultado = ResultadoPrueba.APROBADO),
            prueba(EstadoPrueba.REALIZADA, resultado = ResultadoPrueba.RECHAZADO),
            prueba(EstadoPrueba.NO_REALIZADA, motivo = "Equipo energizado", resultado = ResultadoPrueba.NO_REALIZADA)
        )
        assertEquals(ResultadoPrueba.RECHAZADO, ReglasNegocio.calcularResultadoGlobal(pruebas))
        assertEquals(ResultadoPrueba.NO_REALIZADA, ReglasNegocio.calcularResultadoGlobal(emptyList()))
    }

    // CA-REG-07 [REG-001]: prueba no realizada sin motivo es inválida.
    @Test
    fun noRealizadaSinMotivoDaReg001() {
        assertCodigo(CodigoError.REG_001, ReglasNegocio.validarPruebaCompleta(prueba(EstadoPrueba.NO_REALIZADA)))
    }

    // CA-REG-07 [REG-002]: prueba realizada sin mediciones es inválida.
    @Test
    fun realizadaSinMedicionesDaReg002() {
        assertCodigo(CodigoError.REG_002, ReglasNegocio.validarPruebaCompleta(prueba(EstadoPrueba.REALIZADA)))
    }

    // CA-REG-07: prueba con motivo o con mediciones es válida (sin código de error).
    @Test
    fun pruebasCompletasNoDanError() {
        assertNull(ReglasNegocio.validarPruebaCompleta(prueba(EstadoPrueba.NO_REALIZADA, motivo = "Sin acceso")))
        assertNull(ReglasNegocio.validarPruebaCompleta(
            prueba(EstadoPrueba.REALIZADA, mediciones = listOf(Medicion(campo = "R", valorNumerico = 1.0, unidad = "Ω")))
        ))
    }

    // CA-REG-08: si hay medición posterior al mantenimiento, prevalece sobre la previa.
    @Test
    fun medicionPosteriorPrevalece() {
        val previa = Medicion(campo = "R1", valorNumerico = 1.0, unidad = "GΩ")
        val posterior = Medicion(campo = "R1", valorNumerico = 3.0, unidad = "GΩ", momento = MomentoMedicion.POSTERIOR_MANTENIMIENTO)
        assertEquals(3.0, ReglasNegocio.filtrarMedicionVigente(listOf(previa, posterior), "r1")!!.valorNumerico!!, 0.0)
        assertEquals(1.0, ReglasNegocio.filtrarMedicionVigente(listOf(previa), "R1")!!.valorNumerico!!, 0.0)
        assertNull(ReglasNegocio.filtrarMedicionVigente(listOf(previa), "otro"))
    }

    // CA-REG-09: un transformador genera sus 3 pruebas requeridas.
    @Test
    fun pruebasRequeridasPorTipoDeEquipo() {
        assertEquals(TipoEquipo.TRANSFORMADOR.pruebasRequeridas,
            ReglasNegocio.obtenerPruebasRequeridasPorTipo(TipoEquipo.TRANSFORMADOR.nombre))
    }

    // CA-REG-09: cada prueba conocida tiene su plantilla y las desconocidas una genérica.
    @Test
    fun plantillasDeMediciones() {
        assertEquals(4, ReglasNegocio.generarMedicionesPlantillaPorPrueba("Resistencia de aislación").size)
        assertEquals(5, ReglasNegocio.generarMedicionesPlantillaPorPrueba("Relación de transformación (TTR)").size)
        assertEquals(6, ReglasNegocio.generarMedicionesPlantillaPorPrueba("Resistencia de devanados").size)
        assertEquals(3, ReglasNegocio.generarMedicionesPlantillaPorPrueba("Resistencia de contactos").size)
        assertEquals("Parámetro Principal", ReglasNegocio.generarMedicionesPlantillaPorPrueba("Otra").single().campo)
    }

    // CA-REG-09: solo la resistencia de devanados se califica manualmente.
    @Test
    fun asignacionManualSoloEnDevanados() {
        assertTrue(ReglasNegocio.esPruebaConAsignacionManual("Resistencia de devanados"))
        assertFalse(ReglasNegocio.esPruebaConAsignacionManual("Resistencia de aislación"))
    }

    // CA-REG-10: el correlativo se rellena con ceros a 4 dígitos.
    @Test
    fun numeroDeProtocolo() {
        assertEquals("PT-2026-0042", ReglasNegocio.generarNumeroProtocolo(42))
        assertEquals("PT-2027-1234", ReglasNegocio.generarNumeroProtocolo(1234, 2027))
    }
}
