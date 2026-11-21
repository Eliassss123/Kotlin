package com.induztek.identity;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RoleClient {
    private final WebClient webClient;

    public RoleClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://roles-service").build();
    }

    public RoleResponse findByCode(String code) {
        try {
            return webClient.get()
                    .uri("/api/roles/internal/{code}", code)
                    .retrieve()
                    .bodyToMono(RoleResponse.class)
                    .block(java.time.Duration.ofSeconds(3));
        } catch (RuntimeException exception) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Role service is unavailable", exception);
        }
    }

    public record RoleResponse(String id, String code, String name, String description) {
    }
}
