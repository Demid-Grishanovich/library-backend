package com.library.library_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMs;

    public JwtService(@Value("${application.jwt.secret}") String secret,
                      @Value("${application.jwt.expirationMs}") long expirationMs) {
        this.key = buildKey(secret);
        this.expirationMs = expirationMs;
    }

    private Key buildKey(String secret) {
        try {
            // Превращаем любой ввод в 256-битный ключ (RFC 7518 требует >= 256 бит для HS256)
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] raw = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
            byte[] hashed = sha256.digest(raw); // всегда 32 байта
            return Keys.hmacShaKeyFor(hashed);
        } catch (NoSuchAlgorithmException e) {
            // невозможный случай для SHA-256, но на всякий случай
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public String generate(String subject) {
        return generate(subject, Map.of());
    }

    public String generate(String subject, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isValid(String token) {
        try {
            getSubject(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
