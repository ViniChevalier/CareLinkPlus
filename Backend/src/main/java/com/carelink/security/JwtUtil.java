package com.carelink.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private SecretKey jwtSecretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // 1 hour
    private final long jwtExpirationMs = 1000 * 60 * 60;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Key getSigningKey() {
        return jwtSecretKey;
    }

    public String generateToken(Integer userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Integer getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}