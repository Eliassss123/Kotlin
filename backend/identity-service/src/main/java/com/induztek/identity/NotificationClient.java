package com.induztek.identity;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {
    private final WebClient webClient;

    public NotificationClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://notifications-service").build();
    }

    public void sendPasswordReset(String email, String code) {
        try {
            webClient.post()
                    .uri("/internal/notificaciones/recuperacion")
                    .bodyValue(new RecoveryEmailRequest(email, code))
                    .retrieve()
                    .toBodilessEntity()
                    .block(java.time.Duration.ofSeconds(5));
        } catch (RuntimeException exception) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "Could not deliver the password recovery email", exception);
        }
    }

    public record RecoveryEmailRequest(String email, String code) {
    }
}
