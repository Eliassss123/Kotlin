package com.induztek.protocolos

import com.induztek.protocolos.data.local.UsuarioEntity
import com.induztek.protocolos.model.Equipo
import com.induztek.protocolos.model.EstadoProtocolo
import com.induztek.protocolos.model.PerfilUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnexosDominioTest {

    @Test
    fun testPerfilesUsuario_permisosCorrectosSegunAnexo2() {
        val tecnico = PerfilUsuario.TECNICO_TERRENO
        assertTrue("P-01 debe poder registrar pruebas", tecnico.puedeRegistrarPruebas)
        assertFalse("P-01 no formaliza protocolos", tecnico.puedeFormalizar)
        assertEquals("P-01", tecnico.codigo)

        val ingeniero = PerfilUsuario.INGENIERO_PRUEBAS
        assertTrue("P-02 debe poder registrar pruebas", ingeniero.puedeRegistrarPruebas)
        assertTrue("P-02 debe poder formalizar protocolos", ingeniero.puedeFormalizar)
        assertEquals("P-02", ingeniero.codigo)

        val jefeTecnico = PerfilUsuario.JEFE_TECNICO
        assertTrue("P-03 debe poder registrar pruebas", jefeTecnico.puedeRegistrarPruebas)
        assertTrue("P-03 formaliza y supervisa protocolos", jefeTecnico.puedeFormalizar)
        assertEquals("P-03", jefeTecnico.codigo)

        val cliente = PerfilUsuario.CLIENTE
        assertFalse("P-04 Cliente NO debe registrar pruebas", cliente.puedeRegistrarPruebas)
        assertFalse("P-04 Cliente NO debe formalizar protocolos", cliente.puedeFormalizar)
        assertEquals("P-04", cliente.codigo)
    }

    @Test
    fun testUsuarioEntity_mapeoPerfil() {
        val userCliente = UsuarioEntity(
            id = "USR-004",
            correo = "cliente@plantalampa.cl",
            contrasena = "Cliente2026!",
            nombre = "Mario Gómez",
            perfilCodigo = "P-04",
            empresaCliente = "Planta Lampa"
        )

        assertEquals(PerfilUsuario.CLIENTE, userCliente.getPerfil())
        assertEquals("Planta Lampa", userCliente.empresaCliente)
        assertFalse(userCliente.getPerfil().puedeRegistrarPruebas)

        val userTecnico = UsuarioEntity(
            id = "USR-001",
            correo = "tecnico@induztek.cl",
            contrasena = "Tecnico2026!",
            nombre = "Juan Pérez",
            perfilCodigo = "P-01"
        )
        assertEquals(PerfilUsuario.TECNICO_TERRENO, userTecnico.getPerfil())
        assertTrue(userTecnico.getPerfil().puedeRegistrarPruebas)
    }

    @Test
    fun testEquipo_datosPlacaYTrazabilidadAnexo1Y3() {
        val equipo = Equipo(
            id = "EQ-001",
            codigo = "TR-500KVA-01",
            tagCliente = "TAG-TR-01",
            tipo = "Transformador de Poder",
            cliente = "Planta Lampa",
            ubicacion = "Subestación Principal Norte - Celda 01",
            marca = "WEG / Tusán",
            modelo = "TP-500-23",
            numeroSerie = "SN-2018-9942",
            potenciaTension = "500 kVA / 23 kV - 380 V",
            ultimoEstado = EstadoProtocolo.FORMALIZADO
        )

        assertEquals("TR-500KVA-01", equipo.codigo)
        assertEquals("TAG-TR-01", equipo.tagCliente)
        assertEquals("Planta Lampa", equipo.cliente)
        assertEquals("SN-2018-9942", equipo.numeroSerie)
        assertEquals(EstadoProtocolo.FORMALIZADO, equipo.ultimoEstado)
        assertNotNull(equipo.potenciaTension)
    }
}
