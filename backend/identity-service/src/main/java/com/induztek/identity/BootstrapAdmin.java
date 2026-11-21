package com.induztek.identity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdmin {
    @Bean
    ApplicationRunner bootstrapAdmin(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${identity.bootstrap-admin.email:}") String email,
            @Value("${identity.bootstrap-admin.password:}") String password) {
        return args -> {
            if (email.isBlank() && password.isBlank()) {
                return;
            }
            if (email.isBlank() || password.isBlank() || password.length() < 12) {
                throw new IllegalStateException(
                        "Set both BOOTSTRAP_ADMIN_EMAIL and a BOOTSTRAP_ADMIN_PASSWORD of at least 12 characters");
            }
            String normalizedEmail = email.trim().toLowerCase(java.util.Locale.ROOT);
            if (users.existsByEmailIgnoreCase(normalizedEmail)) {
                return;
            }
            users.save(new UserEntity(
                    java.util.UUID.randomUUID().toString(),
                    normalizedEmail,
                    passwordEncoder.encode(password),
                    "Administrador Induztek",
                    "ADMIN",
                    null,
                    java.time.Instant.now(),
                    true));
        };
    }
}
