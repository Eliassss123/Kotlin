package com.induztek.protocolos.domain

import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Catálogo de códigos de error.
 *
 * Criterios de aceptación:
 * - CA-ERR-01: cada código es único.
 * - CA-ERR-02: cada código sigue el formato PREFIJO-NNN y su prefijo es conocido.
 * - CA-ERR-03: todo error tiene mensaje y razón no vacíos.
 * - CA-ERR-04: el mensaje mostrado lleva el código entre corchetes y completa los parámetros.
 * - CA-ERR-05: se puede buscar un error por su código visible.
 */
class CodigoErrorTest {

    // CA-ERR-01: no puede haber dos errores con el mismo código visible.
    @Test
    fun losCodigosSonUnicos() {
        val codigos = CodigoError.entries.map { it.codigo }
        assertEquals(codigos.size, codigos.toSet().size)
    }

    // CA-ERR-02: el código usa un prefijo conocido seguido de 3 dígitos (p. ej. EQ-002).
    @Test
    fun losCodigosTienenFormatoYPrefijoValido() {
        val prefijos = setOf("AUTH", "PERF", "ADM", "EQ", "VIS", "PRO", "PRU", "REG", "MED", "RED", "APP")
        CodigoError.entries.forEach {
            assertTrue("Formato inválido: ${it.codigo}", it.codigo.matches(Regex("[A-Z]+-\\d{3}")))
            assertTrue("Prefijo desconocido: ${it.codigo}", it.codigo.substringBefore('-') in prefijos)
            assertEquals("El nombre del enum debe coincidir con el código", it.codigo.replace('-', '_'), it.name)
        }
    }

    // CA-ERR-03: todos los errores explican qué ver (mensaje) y por qué pasa (razón).
    @Test
    fun todosTienenMensajeYRazon() {
        CodigoError.entries.forEach {
            assertTrue("${it.codigo} sin mensaje", it.mensaje.isNotBlank())
            assertTrue("${it.codigo} sin razón", it.razon.isNotBlank())
        }
    }

    // CA-ERR-04: formatear() antepone el código y reemplaza los %s.
    @Test
    fun formatearIncluyeCodigoYParametros() {
        assertEquals("[EQ-002] El cliente es obligatorio", CodigoError.EQ_002.formatear())
        assertEquals("[EQ-006] No se encontró el equipo 'TR-01'", CodigoError.EQ_006.formatear("TR-01"))
    }

    // CA-ERR-04: AppException expone el mensaje ya formateado con su código.
    @Test
    fun appExceptionLlevaElMensajeConCodigo() {
        val error = AppException(CodigoError.RED_003, 200)
        assertEquals(CodigoError.RED_003, error.error)
        assertEquals("[RED-003] El servidor respondió sin datos (HTTP 200)", error.message)
    }

    // CA-ERR-05: desdeCodigo encuentra el error o devuelve null si no existe.
    @Test
    fun buscarPorCodigo() {
        assertEquals(CodigoError.VIS_004, CodigoError.desdeCodigo("VIS-004"))
        assertNull(CodigoError.desdeCodigo("XX-999"))
    }
}
