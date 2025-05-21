package com.example.authbe.controller;

import com.example.authbe.dto.GlobalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
public class HealthCheckController {
    
    @GetMapping("/")
    public ResponseEntity<GlobalResponse<String>> healthCheck() {
        GlobalResponse<String> response = GlobalResponse.<String>builder()
                .code(HttpStatus.OK)
                .success(true)
                .message("Service is healthy")
                .data("Auth Service is running")
                .build();
        return ResponseEntity.ok(response);
    }
}
