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

    /**
     * Genera un JWT firmado con los datos del usuario.
     *
     * @param id           ID del usuario.
     * @param email        Email del usuario (sujeto del token).
     * @param rol          Rol del usuario.
     * @param tokenVersion Versión actual del token del usuario. Se usa para invalidar tokens anteriores
     *                     en logout, cambio de contraseña o recuperación de contraseña.
     * @param name         Nombre del usuario.
     * @param lastname     Apellido del usuario.
     * @return JWT firmado.
     */
    public String generateToken(Long id, String email, String rol, Long tokenVersion, String name, String lastname) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("rol", rol);
        claims.put("tokenVersion", tokenVersion);
        claims.put("nombre", name);
        claims.put("apellido", lastname);

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

    public SecretKey generateKey() {
        byte[] keyAsBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyAsBytes);
    }

    /**
     * Extrae el email (subject) del JWT.
     *
     * @param jwt Token JWT firmado.
     * @return Email del usuario.
     */
    public String extractSubject(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    /**
     * Extrae la versión del token ({@code tokenVersion}) del JWT.
     * Retorna {@code null} si el claim no existe (tokens emitidos antes de esta versión).
     *
     * @param jwt Token JWT firmado.
     * @return Versión del token, o {@code null} si no está presente.
     */
    public Long extractTokenVersion(String jwt) {
        Object value = parseClaims(jwt).get("tokenVersion");
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }

    // ─── Privado ──────────────────────────────────────────────────────────────

    private Claims parseClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
