package com.induztek.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentoRepository extends JpaRepository<InstrumentoEntity, String> {
    List<InstrumentoEntity> findByActivoTrue();
}
