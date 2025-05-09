package com.example.authbe.service;

import com.example.authbe.enums.Role;
import com.example.authbe.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        user = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .password("password")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(user, TimeUnit.DAYS.toMillis(1));

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(user, TimeUnit.DAYS.toMillis(1));
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_Success() {
        String token = jwtService.generateToken(user, TimeUnit.DAYS.toMillis(1));
        String role = jwtService.extractRole(token);

        assertEquals("STUDENT", role);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken(user, TimeUnit.DAYS.toMillis(1));
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUser() {
        String token = jwtService.generateToken(user, TimeUnit.DAYS.toMillis(1));
        User differentUser = User.builder()
                .email("different@example.com")
                .fullName("Different User")
                .password("password")
                .role(Role.STUDENT)
                .build();

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }
}
