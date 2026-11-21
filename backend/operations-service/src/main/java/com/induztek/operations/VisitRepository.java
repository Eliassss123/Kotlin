package com.induztek.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitRepository extends JpaRepository<VisitEntity, String> {
    List<VisitEntity> findByClienteIgnoreCase(String cliente);
}
