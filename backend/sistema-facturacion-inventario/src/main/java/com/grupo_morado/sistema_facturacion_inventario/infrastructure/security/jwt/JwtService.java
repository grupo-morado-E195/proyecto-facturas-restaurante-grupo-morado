package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    public String generateToken(Long id, String email, String rol){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("rol", rol);

        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(generateKey())
                .compact();
    }

    public SecretKey generateKey(){
        byte[] keyAsBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyAsBytes);
    }

    public String extractSubject(String jwt){
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
