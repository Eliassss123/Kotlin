package com.induztek.operations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

class CatalogClientTest {
    @Test
    void missingEquipmentIsRejectedBeforeOperationIsSaved() {
        org.springframework.web.reactive.function.client.WebClient.Builder builder =
                org.springframework.web.reactive.function.client.WebClient.builder()
                        .exchangeFunction(request -> Mono.just(
                                org.springframework.web.reactive.function.client.ClientResponse
                                        .create(HttpStatus.OK)
                                        .header("Content-Type", "application/json")
                                        .body("{\"exists\":false}")
                                        .build()));
        CatalogClient client = new CatalogClient(builder);

        assertThrows(ResponseStatusException.class, () -> client.requireEquipment("MISSING"));
    }
}
