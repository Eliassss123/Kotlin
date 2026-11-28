package com.induztek.protocolos.testutil

import com.induztek.protocolos.domain.errores.CodigoError
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

/** Verifica que el mensaje mostrado al usuario empiece con el código esperado, p. ej. "[EQ-002] ...". */
fun assertCodigo(esperado: CodigoError, mensaje: String?) {
    assertNotNull("Se esperaba el error ${esperado.codigo} pero no hubo mensaje", mensaje)
    assertTrue(
        "Se esperaba ${esperado.codigo} pero llegó: $mensaje",
        mensaje!!.startsWith("[${esperado.codigo}]")
    )
}

/** Verifica que el mensaje sea un error HTTP con el código indicado, p. ej. "[HTTP-409] ...". */
fun assertHttp(status: Int, mensaje: String?) {
    assertNotNull("Se esperaba HTTP-$status pero no hubo mensaje", mensaje)
    assertTrue("Se esperaba HTTP-$status pero llegó: $mensaje", mensaje!!.startsWith("[HTTP-$status]"))
}
