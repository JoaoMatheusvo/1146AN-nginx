package com.seuprojeto.authservice.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Component
public class JwtUtil {
    private final Key key;
    private final long expiration = 1000L * 60 * 60 * 24;
    public JwtUtil(@Value("${JWT_SECRET:default-secret-key-please-change}") String secret) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be >=32 chars");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(String subject, List<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .setClaims(Map.of("roles", roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    public Jws<Claims> parseToken(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
