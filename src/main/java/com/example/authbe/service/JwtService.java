package com.example.authbe.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;

public class JwtService {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long defaultExpirationMs = 1000 * 60 * 60; // 1 hour

    public String generateToken(UserDetails userDetails) {
       return "";
    }

    public String generateToken(UserDetails userDetails, long expiryMillis) {
        return  "";
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
       return true;
    }

    public String extractUsername(String token) {
        return "";
    }

    private boolean isTokenExpired(String token) {
       return true;
    }
}
