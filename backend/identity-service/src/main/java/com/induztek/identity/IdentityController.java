package com.induztek.identity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class IdentityController {
    private final IdentityService identity;

    public IdentityController(IdentityService identity) {
        this.identity = identity;
    }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public IdentityService.UserResponse register(@Valid @RequestBody IdentityService.RegisterRequest request) {
        return identity.register(request);
    }

    @PostMapping("/api/auth/login")
    public IdentityService.LoginResponse login(@Valid @RequestBody IdentityService.LoginRequest request) {
        return identity.login(request);
    }

    @PostMapping("/api/auth/password/recovery")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Message requestRecovery(@Valid @RequestBody IdentityService.RecoveryRequest request) {
        identity.requestPasswordReset(request.email());
        return new Message("If the account is active, a recovery code has been sent.");
    }

    @PostMapping("/api/auth/password/reset")
    public Message resetPassword(@Valid @RequestBody IdentityService.ResetPasswordRequest request) {
        identity.resetPassword(request);
        return new Message("Password updated.");
    }

    @PostMapping("/api/auth/password/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyRecoveryCode(@Valid @RequestBody IdentityService.VerifyResetCodeRequest request) {
        identity.verifyPasswordResetCode(request.email(), request.code());
    }

    @GetMapping("/api/usuarios/me")
    public IdentityService.UserResponse currentUser(@AuthenticationPrincipal Jwt jwt) {
        return identity.getCurrentUser(jwt.getSubject());
    }

    @PatchMapping("/api/usuarios/me/perfil")
    public IdentityService.UserResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody IdentityService.ProfileUpdateRequest request) {
        return identity.updateProfile(jwt.getSubject(), request);
    }

    @GetMapping("/api/usuarios")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<IdentityService.UserResponse> users() {
        return identity.listUsers();
    }

    @PatchMapping("/api/usuarios/{id}/estado")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IdentityService.UserResponse setUserState(
            @PathVariable String id, @Valid @RequestBody ActiveRequest request) {
        return identity.setActive(id, request.active());
    }

    public record ActiveRequest(@NotNull Boolean active) {
    }

    public record Message(String message) {
    }
}
