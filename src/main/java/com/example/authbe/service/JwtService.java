package com.example.authbe.service;

import com.example.authbe.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
public class JwtService {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long defaultExpirationMs = 1000 * 60 * 60; // 1 hour

    public String generateToken(UserDetails userDetails) {
        String role = "USER";
        if (userDetails instanceof User) {
            role = ((User) userDetails).getRole().name();
        }
        return generateToken(userDetails, defaultExpirationMs, role);
    }

    public String generateToken(UserDetails userDetails, long expiryMillis) {
        String role = "USER";
        if (userDetails instanceof User) {
            role = ((User) userDetails).getRole().name();
        }
        return generateToken(userDetails, expiryMillis, role);
    }

    public String generateToken(UserDetails userDetails, long expiryMillis, String role) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
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

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
