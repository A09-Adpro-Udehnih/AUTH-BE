package com.example.authbe.controller;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.dto.auth.LoginRequest;
import com.example.authbe.dto.auth.RegisterRequest;
import com.example.authbe.service.AuthService;
import com.example.authbe.dto.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<GlobalResponse<AuthResponse>>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.registerAsync(request)
                .thenApply(authResponse -> {
                    GlobalResponse<AuthResponse> response = GlobalResponse.<AuthResponse>builder()
                            .code(HttpStatus.OK)
                            .success(true)
                            .message("Registration successful")
                            .data(authResponse)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    GlobalResponse<AuthResponse> errorResponse = GlobalResponse.<AuthResponse>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR)
                            .success(false)
                            .message("Registration failed: " + ex.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<GlobalResponse<AuthResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return authService.loginAsync(request)
                .thenApply(authResponse -> {
                    GlobalResponse<AuthResponse> response = GlobalResponse.<AuthResponse>builder()
                            .code(HttpStatus.OK)
                            .success(true)
                            .message("Login successful")
                            .data(authResponse)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    GlobalResponse<AuthResponse> errorResponse = GlobalResponse.<AuthResponse>builder()
                            .code(HttpStatus.UNAUTHORIZED)
                            .success(false)
                            .message("Login failed: " + ex.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                });
    }
} 
