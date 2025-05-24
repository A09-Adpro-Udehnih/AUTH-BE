package com.example.authbe.controller;

import com.example.authbe.dto.GlobalResponse;
import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.dto.auth.LoginRequest;
import com.example.authbe.dto.auth.RegisterRequest;
import com.example.authbe.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.builder()
                .token("mock.jwt.token")
                .email("test@example.com")
                .fullName("Test User")
                .role("STUDENT")
                .build();
    }

    @Test
    void register_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(authService.registerAsync(any(RegisterRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(authResponse));

        // Act
        CompletableFuture<ResponseEntity<GlobalResponse<AuthResponse>>> future = 
            authenticationController.register(registerRequest);
        ResponseEntity<GlobalResponse<AuthResponse>> response = future.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK, body.getCode());
        assertEquals("Registration successful", body.getMessage());
        assertEquals(authResponse, body.getData());
        verify(authService).registerAsync(registerRequest);
    }

    @Test
    void login_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(authService.loginAsync(any(LoginRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(authResponse));

        // Act
        CompletableFuture<ResponseEntity<GlobalResponse<AuthResponse>>> future = 
            authenticationController.login(loginRequest);
        ResponseEntity<GlobalResponse<AuthResponse>> response = future.get();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GlobalResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.OK, body.getCode());
        assertEquals("Login successful", body.getMessage());
        assertEquals(authResponse, body.getData());
        verify(authService).loginAsync(loginRequest);
    }
} 