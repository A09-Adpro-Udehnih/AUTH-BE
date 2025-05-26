package com.example.authbe.service;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.dto.auth.LoginRequest;
import com.example.authbe.dto.auth.RegisterRequest;
import com.example.authbe.enums.Role;
import com.example.authbe.exception.EmailAlreadyRegisteredException;
import com.example.authbe.model.User;
import com.example.authbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private String mockToken;

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

        user = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .build();

        mockToken = "mock.jwt.token";
    }

    @Test
    void registerAsync_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateTokenAsync(any(User.class), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(mockToken));

        // Act
        CompletableFuture<AuthResponse> future = authService.registerAsync(registerRequest);
        AuthResponse response = future.get();

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("STUDENT", response.getRole());
        assertEquals(mockToken, response.getToken());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateTokenAsync(any(User.class), anyLong());
    }

    @Test
    void registerAsync_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        CompletableFuture<AuthResponse> future = authService.registerAsync(registerRequest);
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            future.get();
        });
        assertTrue(thrown.getCause() instanceof EmailAlreadyRegisteredException);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginAsync_Success() throws ExecutionException, InterruptedException {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateTokenAsync(any(User.class), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(mockToken));

        // Act
        CompletableFuture<AuthResponse> future = authService.loginAsync(loginRequest);
        AuthResponse response = future.get();

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
        assertEquals("STUDENT", response.getRole());
        assertEquals(mockToken, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(jwtService).generateTokenAsync(any(User.class), anyLong());
    }

    @Test
    void loginAsync_UserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        CompletableFuture<AuthResponse> future = authService.loginAsync(loginRequest);
        ExecutionException thrown = assertThrows(ExecutionException.class, () -> {
            future.get();
        });
        assertTrue(thrown.getCause() instanceof BadCredentialsException);
        assertTrue(thrown.getCause().getMessage().contains("Invalid email or password"));
    }
} 