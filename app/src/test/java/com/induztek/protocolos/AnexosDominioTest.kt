// ============================================================================
// ARCHIVO : test/.../AnexosDominioTest.kt
// CAPA    : Pruebas unitarias
// RESUMEN : valida el cumplimiento de las reglas de negocio de los Anexos 1, 2 y 3:
//           - Perfiles P-01 a P-04 y sus permisos (Anexo 2)
//           - Restricción del cliente: solo lectura (P-04)
//           - Catálogo de equipos y trazabilidad de placa (Anexo 1 y 3)
// ============================================================================

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
        // P-01 Técnico de terreno: registra pruebas, no formaliza
        val tecnico = PerfilUsuario.TECNICO_TERRENO
        assertTrue("P-01 debe poder registrar pruebas", tecnico.puedeRegistrarPruebas)
        assertFalse("P-01 no formaliza protocolos", tecnico.puedeFormalizar)
        assertEquals("P-01", tecnico.codigo)

        // P-02 Ingeniero de pruebas: registra y formaliza
        val ingeniero = PerfilUsuario.INGENIERO_PRUEBAS
        assertTrue("P-02 debe poder registrar pruebas", ingeniero.puedeRegistrarPruebas)
        assertTrue("P-02 debe poder formalizar protocolos", ingeniero.puedeFormalizar)
        assertEquals("P-02", ingeniero.codigo)

        // P-03 Jefe técnico: supervisión global y formalización
        val jefeTecnico = PerfilUsuario.JEFE_TECNICO
        assertTrue("P-03 debe poder registrar pruebas", jefeTecnico.puedeRegistrarPruebas)
        assertTrue("P-03 formaliza y supervisa protocolos", jefeTecnico.puedeFormalizar)
        assertEquals("P-03", jefeTecnico.codigo)

        // P-04 Usuario cliente: SOLO CONSULTA (no registra ni formaliza)
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
