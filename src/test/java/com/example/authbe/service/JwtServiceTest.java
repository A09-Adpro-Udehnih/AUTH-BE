package com.example.authbe.service;

import com.example.authbe.enums.Role;
import com.example.authbe.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;
    private UUID userId;

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
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateToken_WithUserDetailsAndExpiry_Success() {
        String token = jwtService.generateToken(user, 3600000L); // 1 hour

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateToken_WithInvalidUserDetails_ThrowsException() {
        UserDetails invalidUser = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };

        assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(invalidUser));
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void extractRole_Success() {
        String token = jwtService.generateToken(user);
        String role = jwtService.extractRole(token);

        assertEquals("STUDENT", role);
    }

    @Test
    void extractUserId_Success() {
        String token = jwtService.generateToken(user);
        UUID extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractFullName_Success() {
        String token = jwtService.generateToken(user);
        String fullName = jwtService.extractFullName(token);

        assertEquals("Test User", fullName);
    }

    @Test
    void isTokenValid_Success() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUser() {
        String token = jwtService.generateToken(user);
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
        String token = jwtService.generateToken(user, -1000L); // Expired token
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }
}
