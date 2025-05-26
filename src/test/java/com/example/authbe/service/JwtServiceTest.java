package com.example.authbe.service;

import com.example.authbe.enums.Role;
import com.example.authbe.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;
    private UUID userId;
    private static final long EXPIRY_TIME = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("password")
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void generateToken_WithUserDetails_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_WithUserDetailsAndExpiry_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME); // 1 hour

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateTokenAsync_Success() throws Exception {
        CompletableFuture<String> tokenFuture = jwtService.generateTokenAsync(user, EXPIRY_TIME);
        String token = tokenFuture.get();

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        String role = jwtService.extractRole(token);

        assertEquals("STUDENT", role);
    }

    @Test
    void extractUserId_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        UUID extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractFullName_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        String fullName = jwtService.extractFullName(token);

        assertEquals("Test User", fullName);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUser() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        User differentUser = User.builder()
                .id(UUID.randomUUID())
                .email("different@example.com")
                .fullName("Different User")
                .password("password")
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken() {
        String token = jwtService.generateToken(user, -1000L);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_MalformedJwt() {
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        boolean isValid = jwtService.isTokenValid(malformedToken, user);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_UnsupportedJwt() {
        String unsupportedToken = "eyJhbGciOiJOT1RTUFAiLCJ0eXAiOiJKV1QifQ.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.signature";
        boolean isValid = jwtService.isTokenValid(unsupportedToken, user);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_IllegalArgument() {
        boolean isValid = jwtService.isTokenValid(null, user);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_SignatureException() {
        String tokenWithInvalidSignature = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.invalid_signature";
        boolean isValid = jwtService.isTokenValid(tokenWithInvalidSignature, user);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ValidTokenButWrongUsername() {
        String token = jwtService.generateToken(user, EXPIRY_TIME);
        
        User differentUser = User.builder()
                .id(UUID.randomUUID())
                .email("wrong@example.com") 
                .fullName("Wrong User")
                .password("password")
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);
        assertFalse(isValid);
    }
}
