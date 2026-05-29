package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt;

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

/**
 * Filtro JWT que se ejecuta una vez por request.
 * Lee el token del header Authorization, lo valida y carga el usuario
 * en el SecurityContext para que Spring Security pueda hacer el control de acceso.
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

            // Solo autenticar si el email es válido y no hay autenticación previa en el contexto
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userDAO.findByEmail(email).ifPresent(userEntity -> {
                    SecurityUser userDetails = new SecurityUser(userEntity);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                });
            }

        } catch (Exception e) {
            // Token inválido o expirado — se deja sin autenticación; Spring Security devolverá 401
            log.warn("Token JWT inválido o expirado en request a {}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
