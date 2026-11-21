package com.induztek.operations;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, String> {
    List<TestEntity> findByProtocoloId(String protocoloId);
    List<TestEntity> findByEquipoCodigo(String equipoCodigo);
}
