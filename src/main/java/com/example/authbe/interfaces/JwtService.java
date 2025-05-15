package com.example.authbe.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);

    String generateToken(UserDetails userDetails, long expirationTimeInMillis);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenExpired(String token);
}
