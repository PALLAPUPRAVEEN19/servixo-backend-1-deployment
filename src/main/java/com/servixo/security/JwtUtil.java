package com.servixo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // ✅ IMPORTANT (so @Autowired works)
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ✅ NEW METHOD (WITH ROLE)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // 🔥 store role inside token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ KEEP OLD METHOD (OPTIONAL)
    public String generateToken(String email) {
        return generateToken(email, "USER");
    }

    // ✅ EXTRACT EMAIL
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ EXTRACT ROLE
    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    // ✅ COMMON METHOD
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}