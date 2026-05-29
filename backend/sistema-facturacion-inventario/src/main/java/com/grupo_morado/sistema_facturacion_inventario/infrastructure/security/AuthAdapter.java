package com.grupo_morado.sistema_facturacion_inventario.infrastructure.security;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.AuthProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TemporaryPasswordExpiredException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.UserMapper;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.UserDAO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthAdapter implements AuthProviderPort {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserDAO userDAO;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthLoginResultDTO authenticate(String email, String password) {
        try {
            UsernamePasswordAuthenticationToken userToken =
                    new UsernamePasswordAuthenticationToken(email, password);
            Authentication authUser = authenticationManager.authenticate(userToken);

            SecurityUser userDetails = (SecurityUser) authUser.getPrincipal();
            User user = userDetails.getUser();
            String role = extractRole(userDetails);

            String token = jwtService.generateToken(user.getId(), userDetails.getUsername(), role, user.getTokenVersion(), user.getName(), user.getLastname());
            return new AuthLoginResultDTO(token, false);

        } catch (DisabledException e) {
            log.warn("Intento de login con usuario inactivo: {}", email);
            throw new DisabledException("El usuario se encuentra inactivo en el sistema.");
        } catch (BadCredentialsException e) {
            return authenticateWithTemporaryPassword(email, password);
        }
    }

    @Override
    public User register(com.grupo_morado.sistema_facturacion_inventario.domain.models.User user,
                         Role role, String passwordEncoded) {
        User userEntity = userMapper.modelToEntity(user);
        userEntity.setPassword(passwordEncoded);
        userEntity.setStatus(StatusEnum.ACTIVO);
        userEntity.setRole(role);
        return userDAO.save(userEntity);
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    /**
     * Intenta autenticar usando la contraseña temporal del usuario.
     * Solo se invoca cuando la autenticación normal ha fallado.
     */
    private AuthLoginResultDTO authenticateWithTemporaryPassword(String email, String password) {
        Optional<User> userOptional = userDAO.findByEmail(email);

        if (userOptional.isEmpty()) {
            // No existe el usuario — misma excepción para no filtrar información
            throw new BadCredentialsException("Las credenciales son incorrectas");
        }

        User userEntity = userOptional.get();

        // Verificar si hay contraseña temporal configurada
        if (userEntity.getTemporaryPassword() == null) {
            throw new BadCredentialsException("Las credenciales son incorrectas");
        }

        // Verificar si la contraseña temporal ha expirado
        if (userEntity.getTemporaryPasswordExpiration() == null
                || LocalDateTime.now().isAfter(userEntity.getTemporaryPasswordExpiration())) {
            log.warn("Intento de login con contraseña temporal expirada para el usuario: {}", email);
            throw new TemporaryPasswordExpiredException(
                    "La contraseña temporal ha expirado. Por favor solicita un nuevo restablecimiento."
            );
        }

        // Validar contraseña temporal con BCrypt
        if (!passwordEncoder.matches(password, userEntity.getTemporaryPassword())) {
            throw new BadCredentialsException("Las credenciales son incorrectas");
        }

        // Contraseña temporal válida — generar JWT con flag de cambio obligatorio
        String role = userEntity.getRole() != null
                ? "ROLE_" + userEntity.getRole().getName().toUpperCase()
                : null;

        String token = jwtService.generateToken(
                userEntity.getId(),
                userEntity.getEmail(),
                role,
                userEntity.getTokenVersion(),
                userEntity.getName(),
                userEntity.getLastname()
        );

        log.info("Login con contraseña temporal exitoso para el usuario: {}. Cambio de contraseña requerido.", email);
        return new AuthLoginResultDTO(token, true);
    }

    /**
     * Extrae el primer rol del {@link SecurityUser} autenticado.
     */
    private String extractRole(SecurityUser userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }
}
