package com.induztek.protocolos

import com.induztek.protocolos.data.remote.*
import com.induztek.protocolos.model.EstadoProtocolo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteMappersTest {

    @Test
    fun testUserResponseDtoToProfileMapping() {
        val dto = UserResponseDto(
            id = "USR-001",
            email = "admin@induztek.cl",
            name = "Administrador Induztek",
            roleCode = "ADMIN",
            perfilCodigo = "P-03",
            company = "Induztek SpA",
            termsAccepted = true,
            termsAcceptedAt = null,
            active = true,
            photoUrl = "https://example.com/photo.jpg",
            biography = "Admin bio"
        )

        val profile = dto.toProfile()
        assertEquals("USR-001", profile.id)
        assertEquals("admin@induztek.cl", profile.correo)
        assertEquals("Administrador Induztek", profile.nombre)
        assertEquals("P-03", profile.perfilCodigo)
        assertEquals("Induztek SpA", profile.empresaCliente)
        assertTrue(profile.activo)
        assertEquals("https://example.com/photo.jpg", profile.fotoPerfil)
    }

    @Test
    fun testEquipoDtoToDomainMapping() {
        val dto = EquipoDto(
            id = "EQ-1",
            codigo = "TR-500KVA-01",
            tagCliente = "TAG-01",
            tipo = "Transformador",
            cliente = "Minera Andes",
            ubicacion = "Subestación",
            marca = "WEG",
            modelo = "TP",
            numeroSerie = "SN123",
            potenciaTension = "500 kVA",
            anio = "2020",
            tensiones = "23kV",
            tap = "3",
            conexion = "Dyn11",
            ultimoEstado = "Formalizado"
        )

        val equipo = dto.toDomain()
        assertEquals("TR-500KVA-01", equipo.codigo)
        assertEquals("Minera Andes", equipo.cliente)
        assertEquals(EstadoProtocolo.FORMALIZADO, equipo.ultimoEstado)
    }
}
