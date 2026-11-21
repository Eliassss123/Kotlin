package com.induztek.operations;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActaRepository extends JpaRepository<ActaEntity, String> {
    Optional<ActaEntity> findByVisitaId(String visitaId);
}
