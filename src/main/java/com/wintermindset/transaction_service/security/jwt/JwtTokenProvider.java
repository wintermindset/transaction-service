package com.wintermindset.transaction_service.security.jwt;

import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String SECRET_WORD =
        "TEMPLATE_SECRET_WORD_123456789_QWERTY_TEMPLATE_SECRET_WORD";

    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60;

    private final SecretKey secretKey;

    public JwtTokenProvider() {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_WORD.getBytes());
    }

    public String generateToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public UUID extractUserId(String token) {
        String subject = extractAllClaims(token)
            .getPayload()
            .getSubject();
        return UUID.fromString(subject);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token)
            .getPayload()
            .getExpiration();
        return expiration.before(new Date());
    }

    private Jws<Claims> extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token);
    }
}