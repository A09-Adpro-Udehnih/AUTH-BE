package com.example.authbe.service;

import com.example.authbe.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class JwtService {
    private final Key key = Keys.hmacShaKeyFor(
    Base64.getDecoder().decode(System.getenv("JWT_TOKEN") != null ? System.getenv("JWT_TOKEN") : "secretsampai256bitsinicumanbuattestingbiardigithubsoalnyagabacaenv")
);

    public String generateToken(User user, long expiryMillis) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("email", user.getEmail())
                .claim("fullName", user.getFullName())
                .claim("role", user.getRole().name())
                .claim("createdAt", user.getCreatedAt().toString())
                .claim("updatedAt", user.getUpdatedAt().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername());
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException e) {
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

    @Async("taskExecutor")
    public CompletableFuture<String> generateTokenAsync(User user, long expiryMillis) {
        return CompletableFuture.supplyAsync(() -> generateToken(user, expiryMillis));
    }
}
