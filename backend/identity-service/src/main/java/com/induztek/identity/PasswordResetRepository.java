package com.induztek.identity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, String> {
    List<PasswordResetEntity> findByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(String email);
    Optional<PasswordResetEntity> findFirstByEmailIgnoreCaseAndUsedFalseOrderByExpiresAtDesc(String email);
}
