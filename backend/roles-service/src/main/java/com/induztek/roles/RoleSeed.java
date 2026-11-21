package com.induztek.roles;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleSeed {
    @Bean
    ApplicationRunner seedRoles(RoleRepository roles) {
        return args -> {
            saveIfMissing(roles, "P-01", "FIELD_TECHNICIAN", "Técnico de terreno", "Registro de visitas y protocolos");
            saveIfMissing(roles, "P-02", "TEST_ENGINEER", "Ingeniero de pruebas", "Revisión y formalización de protocolos");
            saveIfMissing(roles, "P-03", "ADMIN", "Jefe técnico", "Administración y formalización de protocolos");
            saveIfMissing(roles, "P-04", "CLIENT", "Cliente", "Consulta de información de su empresa");
        };
    }

    private void saveIfMissing(RoleRepository roles, String id, String code, String name, String description) {
        if (roles.findByCode(code).isEmpty()) {
            roles.save(new RoleEntity(id, code, name, description));
        }
    }
}
