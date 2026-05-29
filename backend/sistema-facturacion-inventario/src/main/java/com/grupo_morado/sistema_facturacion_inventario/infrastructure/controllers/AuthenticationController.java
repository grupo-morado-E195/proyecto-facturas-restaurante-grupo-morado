package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.AuthService;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthChangePasswordDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthResetPasswordDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthUpdatePasswordDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.mappers.AuthenticationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthService authService;
    private final AuthenticationMapper authenticationMapper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDTO auth) {
        log.info("Intento de inicio de sesion para el usuario con correo: {}", auth.email());
        User user = authenticationMapper.dtoToModelLogin(auth);
        AuthLoginResultDTO resultAuth = authService.login(user);
        log.info("Autenticacion exitosa para el usuario con correo: {}", auth.email());
        return ResponseEntity.ok(resultAuth);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRegisterDTO auth) {
        log.info("Intento de registro para el usuario con correo: {}", auth.email());
        AuthRegisterResultDTO resultAuth = authService.register(auth);
        log.info("Registro exitoso para el usuario con correo: {}", auth.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultAuth);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid AuthResetPasswordDTO request) {
        log.info("Solicitud de restablecimiento de contraseña para el correo: {}", request.email());
        authService.requestPasswordReset(request.email());
        log.info("Contraseña temporal enviada exitosamente al correo: {}", request.email());
        return ResponseEntity.ok(
                Map.of("message", "Se ha enviado una contraseña temporal a tu correo electrónico. " +
                        "Expirará en 5 minutos.")
        );
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid AuthUpdatePasswordDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("Solicitud de actualización de contraseña para el usuario autenticado: {}", email);
        authService.updatePassword(email, request.newPassword());
        log.info("Contraseña actualizada exitosamente para el usuario: {}", email);
        return ResponseEntity.ok(
                Map.of("message", "Contraseña actualizada correctamente. Ya puedes iniciar sesión con tu nueva contraseña.")
        );
    }

    /**
     * Cambia la contraseña de un usuario autenticado desde su panel de usuario.
     *
     * <p>Requiere:
     * <ul>
     *   <li>Token JWT válido en el header {@code Authorization: Bearer <token>}.</li>
     *   <li>La contraseña actual del usuario para verificar identidad.</li>
     *   <li>La nueva contraseña y su confirmación.</li>
     * </ul>
     *
     * <p>Tras el cambio exitoso, el frontend debe descartar el token actual y
     * redirigir al login para una nueva autenticación.
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid AuthChangePasswordDTO request) {
        // El email siempre viene del JWT autenticado, nunca del body
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("Solicitud de cambio de contraseña para el usuario autenticado: {}", email);
        authService.changePassword(email, request.currentPassword(), request.newPassword(), request.confirmPassword());
        log.info("Contraseña cambiada exitosamente para el usuario: {}", email);

        return ResponseEntity.ok(
                Map.of("message", "Contraseña actualizada correctamente. Por seguridad, deberás iniciar sesión nuevamente.")
        );
    }

    /**
     * Cierra la sesión del usuario autenticado.
     *
     * <p>Invalida todos los JWT activos del usuario incrementando {@code tokenVersion}
     * en base de datos. Requiere un token JWT válido en el header
     * {@code Authorization: Bearer <token>}. El frontend debe descartar el token localmente
     * tras recibir la respuesta exitosa.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.info("Solicitud de logout para el usuario autenticado: {}", email);
        authService.logout(email);
        log.info("Logout exitoso. Tokens invalidados para el usuario: {}", email);

        return ResponseEntity.ok(
                Map.of("message", "Sesión cerrada correctamente. Por favor inicia sesión nuevamente.")
        );
    }
}
