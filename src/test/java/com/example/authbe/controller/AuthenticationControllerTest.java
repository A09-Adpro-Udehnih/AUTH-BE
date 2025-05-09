package com.example.authbe.controller;

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
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        registerRequest.setFullName("Test User");
        registerRequest.setPassword("password123");
        registerRequest.setRole("STUDENT");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.builder()
                .email("test@example.com")
                .fullName("Test User")
                .role("STUDENT")
                .token("jwt.token.here")
                .build();
    }

    @Test
    void register_Success() {
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authenticationController.register(registerRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(authResponse, response.getBody());
        verify(authService).register(registerRequest);
    }

    @Test
    void login_Success() {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authenticationController.login(loginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(authResponse, response.getBody());
        verify(authService).login(loginRequest);
    }
} 