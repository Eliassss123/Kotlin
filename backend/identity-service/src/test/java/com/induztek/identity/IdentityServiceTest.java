package com.induztek.identity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.server.ResponseStatusException;

class IdentityServiceTest {
    @Test
    void registrationRequiresTermsBeforeCreatingAnAccount() {
        UserRepository users = org.mockito.Mockito.mock(UserRepository.class);
        PasswordResetRepository resets = org.mockito.Mockito.mock(PasswordResetRepository.class);
        RoleClient roles = org.mockito.Mockito.mock(RoleClient.class);
        NotificationClient notifications = org.mockito.Mockito.mock(NotificationClient.class);
        IdentityService service = new IdentityService(
                users, resets, new BCryptPasswordEncoder(), org.mockito.Mockito.mock(JwtEncoder.class),
                roles, notifications, 480, 10, true);

        assertThrows(ResponseStatusException.class, () -> service.register(
                new IdentityService.RegisterRequest("tech@example.com", "A-strong-password", "Tech", null, false)));

        verifyNoInteractions(users, roles);
    }

    @Test
    void loginRejectsPendingAccounts() {
        UserRepository users = org.mockito.Mockito.mock(UserRepository.class);
        PasswordResetRepository resets = org.mockito.Mockito.mock(PasswordResetRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserEntity pending = new UserEntity(
                "user-1", "tech@example.com", encoder.encode("A-strong-password"), "Tech",
                "FIELD_TECHNICIAN", null, Instant.now(), false);
        when(users.findByEmailIgnoreCase("tech@example.com")).thenReturn(Optional.of(pending));
        IdentityService service = new IdentityService(
                users, resets, encoder, org.mockito.Mockito.mock(JwtEncoder.class),
                org.mockito.Mockito.mock(RoleClient.class), org.mockito.Mockito.mock(NotificationClient.class),
                480, 10, true);

        assertThrows(ResponseStatusException.class, () -> service.login(
                new IdentityService.LoginRequest("TECH@example.com", "A-strong-password")));
    }
}
