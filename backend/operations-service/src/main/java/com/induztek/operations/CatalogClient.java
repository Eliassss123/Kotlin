package com.induztek.operations;

import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CatalogClient {
    private final WebClient webClient;

    public CatalogClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://catalog-service").build();
    }

    public void requireEquipment(String code) {
        try {
            ExistsResponse response = webClient.get()
                    .uri("/api/equipos/internal/{code}", code)
                    .retrieve()
                    .bodyToMono(ExistsResponse.class)
                    .block(Duration.ofSeconds(3));
            if (response == null || !response.exists()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found: " + code);
            }
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalog service is unavailable", exception);
        }
    }

    private record ExistsResponse(boolean exists) {
    }
}
