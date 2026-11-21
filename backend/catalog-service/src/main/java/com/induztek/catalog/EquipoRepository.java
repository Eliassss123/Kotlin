package com.induztek.catalog;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepository extends JpaRepository<EquipoEntity, String> {
    Optional<EquipoEntity> findByCodigo(String codigo);
    boolean existsByUbicacionAndTagCliente(String ubicacion, String tagCliente);
    boolean existsByUbicacionAndNumeroSerie(String ubicacion, String numeroSerie);
}
