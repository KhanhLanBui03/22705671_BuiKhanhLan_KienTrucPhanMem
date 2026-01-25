package com.se.jwt.util;

import com.se.jwt.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;



@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateAccessToken(com.se.jwt.model.User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getUsername())
                .claim("roles", user.getRoles()
                        .stream()
                        .map(r ->  r.getName())
                        .toList())

                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
    // ===== Parse token =====
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===== Helpers =====
    public String extractEmail(String token) {
        return parseToken(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        return parseToken(token).get("roles", List.class);
    }

    public boolean isTokenExpired(String token) {
        return parseToken(token)
                .getExpiration()
                .before(new Date());
    }


}