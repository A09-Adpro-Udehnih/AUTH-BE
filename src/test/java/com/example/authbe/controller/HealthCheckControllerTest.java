package com.example.authbe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class HealthCheckControllerTest {

    private final HealthCheckController healthCheckController = new HealthCheckController();

    @Test
    void healthCheck_ShouldReturnOkResponse() {
        ResponseEntity<String> response = healthCheckController.healthCheck();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Rollback", response.getBody());
    }
} 