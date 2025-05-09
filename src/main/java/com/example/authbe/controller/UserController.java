package com.example.authbe.controller;

import com.example.authbe.dto.auth.AuthResponse;
import com.example.authbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            Authentication authentication,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.updateProfile(userId, fullName, currentPassword, newPassword));
    }
} 