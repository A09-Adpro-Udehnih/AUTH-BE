package com.example.authbe.service;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.enums.Role;
import com.example.authbe.model.User;
import com.example.authbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test@example.com"));
    }

    @Test
    void getProfile_Success() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        AuthResponse response = userService.getProfile(userId);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void getProfile_UserNotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getProfile(userId));
    }
} 