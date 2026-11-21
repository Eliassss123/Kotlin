package com.induztek.roles;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleRepository roles;

    public RoleController(RoleRepository roles) {
        this.roles = roles;
    }

    @GetMapping
    public List<RoleResponse> list() {
        return roles.findAll().stream().map(RoleResponse::from).toList();
    }

    @GetMapping("/{code}")
    public RoleResponse get(@PathVariable String code) {
        return roles.findByCode(code)
                .map(RoleResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    @GetMapping("/internal/{code}")
    public RoleResponse getInternal(@PathVariable String code) {
        return get(code);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        if (roles.findByCode(request.code()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role code already exists");
        }
        return RoleResponse.from(roles.save(new RoleEntity(
                request.id(), request.code(), request.name(), request.description())));
    }

    public record RoleRequest(
            @NotBlank String id,
            @NotBlank String code,
            @NotBlank String name,
            String description) {
    }

    public record RoleResponse(String id, String code, String name, String description) {
        static RoleResponse from(RoleEntity role) {
            return new RoleResponse(role.getId(), role.getCode(), role.getName(), role.getDescription());
        }
    }
}
