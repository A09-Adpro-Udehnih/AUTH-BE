package com.example.authbe.service;

import com.example.authbe.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class JwtService {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final static long defaultExpirationMs = 1000L * 60 * 60; // 1 hour

    public String generateToken(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            user = (User) userDetails;
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");
            return generateToken(user, defaultExpirationMs, role);
        }
        throw new IllegalArgumentException("UserDetails must be an instance of User");
    }

    public String generateToken(UserDetails userDetails, long expiryMillis) {
        if (userDetails instanceof User user) {
            user = (User) userDetails;
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");
            return generateToken(user, expiryMillis, role);
        }
        throw new IllegalArgumentException("UserDetails must be an instance of User");
    }

    public String generateToken(User user, long expiryMillis, String role) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .claim("role", role)
                .claim("createdAt", user.getCreatedAt().toString())
                .claim("updatedAt", user.getUpdatedAt().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public UUID extractUserId(String token) {
        String userIdStr = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    public String extractFullName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("fullName", String.class);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateTokenAsync(User user, long expiryMillis, String role) {
        return CompletableFuture.supplyAsync(() -> generateToken(user, expiryMillis, role));
    }

    @Async("taskExecutor")
    public CompletableFuture<Boolean> isTokenValidAsync(String token, UserDetails userDetails) {
        return CompletableFuture.supplyAsync(() -> isTokenValid(token, userDetails));
    }

    @Async("taskExecutor")
    public CompletableFuture<String> extractUsernameAsync(String token) {
        return CompletableFuture.supplyAsync(() -> extractUsername(token));
    }
}
