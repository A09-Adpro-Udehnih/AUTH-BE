package com.example.authbe.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testGenerateToken() {
        UserDetails user = User.withUsername("testuser").password("password").roles("USER").build();
        String token = jwtService.generateToken(user);
        assertNotNull(token);
    }

    @Test
    void testValidateToken_Valid() {
        UserDetails user = User.withUsername("testuser").password("password").roles("USER").build();
        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void testValidateToken_Invalid() {
        UserDetails user = User.withUsername("testuser").password("password").roles("USER").build();
        String token = jwtService.generateToken(user);
        // Tamper the token
        String invalidToken = token + "tampered";
        assertFalse(jwtService.isTokenValid(invalidToken, user));
    }

    @Test
    void testValidateToken_Expired() throws InterruptedException {
        UserDetails user = User.withUsername("testuser").password("password").roles("USER").build();
        String token = jwtService.generateToken(user, 1); // 1 ms expiry
        Thread.sleep(5);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }
}
