package com.induztek.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EquipoEntityTest {
    @Test
    void updatePreservesIdentityWhileChangingEquipmentDetails() {
        EquipoEntity equipo = new EquipoEntity(
                "id-1", "EQ-1", "TAG-1", "Transformador", "Cliente A", "Planta",
                "Marca", "Modelo", "SN-1", "500 kVA", "2020", "23 kV", "3", "Dyn11", "Registrado");

        equipo.update(new CatalogController.EquipoRequest(
                "EQ-2", "TAG-2", "Relé", "Cliente B", "Sala", "Otra marca", "Otro modelo",
                "SN-2", "125 V", "2022", "125 V", "N/A", "Secundaria", "Aprobado"));

        assertEquals("id-1", equipo.getId());
        assertEquals("EQ-2", equipo.getCodigo());
        assertEquals("Relé", equipo.getTipo());
        assertEquals("Cliente B", equipo.getCliente());
    }
}
