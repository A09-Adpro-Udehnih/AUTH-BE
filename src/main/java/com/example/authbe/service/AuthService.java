package com.example.authbe.service;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.dto.auth.LoginRequest;
import com.example.authbe.dto.auth.RegisterRequest;
import com.example.authbe.enums.Role;
import com.example.authbe.exception.EmailAlreadyRegisteredException;
import com.example.authbe.model.User;
import com.example.authbe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<AuthResponse> registerAsync(RegisterRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyRegisteredException(request.getEmail());
            }

            var user = User.builder()
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.STUDENT)
                    .build();

            userRepository.save(user);
            CompletableFuture<String> tokenFuture = jwtService.generateTokenAsync(user, TimeUnit.DAYS.toMillis(1));
            String token = tokenFuture.join();

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .build();
        });
    }

    @Async("taskExecutor")
    public CompletableFuture<AuthResponse> loginAsync(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid email or password");
            }

            CompletableFuture<String> tokenFuture = jwtService.generateTokenAsync(user, TimeUnit.DAYS.toMillis(1));
            String token = tokenFuture.join();

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole().name())
                    .build();
        });
    }
} 