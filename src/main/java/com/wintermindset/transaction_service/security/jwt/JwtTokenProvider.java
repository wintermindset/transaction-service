package com.wintermindset.transaction_service.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtTokenProvider {
    
    private static String SECRET_WORD = "TEMPLATE_SECRET_WORD_123456789_QWERTY";
    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60;

    private final SecretKey secretKey;

    public JwtTokenProvider() {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_WORD.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getPayload().getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    private Jws<Claims> extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}
