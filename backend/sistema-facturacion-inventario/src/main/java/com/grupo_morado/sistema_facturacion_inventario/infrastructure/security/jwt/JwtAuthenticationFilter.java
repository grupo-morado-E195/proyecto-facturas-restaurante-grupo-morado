package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.UserDAO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.SecurityUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filtro JWT que se ejecuta una vez por request.
 *
 * <p>Flujo de validación:
 * <ol>
 *   <li>Lee el token del header {@code Authorization: Bearer <token>}.</li>
 *   <li>Extrae el email (subject) y la {@code tokenVersion} del JWT.</li>
 *   <li>Carga el usuario desde la base de datos.</li>
 *   <li>Compara la {@code tokenVersion} del JWT con la almacenada en la BD.
 *       Si no coinciden (logout o cambio de contraseña previo), el token se
 *       considera inválido y no se establece autenticación.</li>
 *   <li>Si todo es válido, establece el usuario en el {@link SecurityContextHolder}.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDAO userDAO;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Si no hay cabecera Bearer, pasar al siguiente filtro sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtService.extractSubject(jwt);
            final Long tokenVersionFromJwt = jwtService.extractTokenVersion(jwt);

            // Solo autenticar si el email es válido y no hay autenticación previa en el contexto
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> userOptional = userDAO.findByEmail(email);

                if (userOptional.isPresent()) {
                    User userEntity = userOptional.get();

                    // ── Validación de tokenVersion ────────────────────────────
                    // Si el tokenVersion del JWT no coincide con el de la BD,
                    // el token ha sido invalidado (logout / cambio de contraseña).
                    // Los tokens emitidos antes de esta versión (sin el claim) también son rechazados.
                    if (tokenVersionFromJwt == null || !tokenVersionFromJwt.equals(userEntity.getTokenVersion())) {
                        log.warn("Token JWT rechazado para '{}': tokenVersion inválida (token={}, bd={})",
                                email, tokenVersionFromJwt, userEntity.getTokenVersion());
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // ── Token válido — establecer autenticación en el contexto ─
                    SecurityUser userDetails = new SecurityUser(userEntity);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // Token inválido o expirado — se deja sin autenticación; Spring Security devolverá 401
            log.warn("Token JWT inválido o expirado en request a {}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
