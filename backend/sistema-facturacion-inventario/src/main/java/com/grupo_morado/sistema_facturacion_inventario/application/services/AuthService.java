package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.AuthUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.AuthProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.EmailNotificationPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.PasswordEncryptPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.RoleProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidCurrentPasswordException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidEmailException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.PasswordConfirmationMismatchException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.PasswordSameAsCurrentException;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.mappers.AuthenticationMapper;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Role;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private static final int TEMPORARY_PASSWORD_LENGTH = 12;
    private static final int TEMPORARY_PASSWORD_EXPIRATION_MINUTES = 5;
    private static final String TEMPORARY_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*";

    private final AuthProviderPort authProviderPort;
    private final PasswordEncryptPort passwordEncryptPort;
    private final RoleProviderPort roleProviderPort;
    private final UserProviderPort userProviderPort;
    private final EmailNotificationPort emailNotificationPort;
    private final AuthenticationMapper authenticationMapper;
    private final UserMapper userMapper;

    @Override
    public AuthLoginResultDTO login(User user) {
        return authProviderPort.authenticate(user.getEmail(), user.getPassword());
    }

    @Override
    public AuthRegisterResultDTO register(AuthRegisterDTO user) {
        Optional<Role> role = roleProviderPort.findById(user.roleID());
        if (role.isEmpty()) {
            throw new NotFoundException("el rol con id '" + user.roleID() + "' no fue encontrado.");
        }
        String passwordEncoded = passwordEncryptPort.encryptPassword(user.password());
        User userModel = authenticationMapper.dtoToModelRegister(user);
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity =
                authProviderPort.register(userModel, role.get(), passwordEncoded);
        return userMapper.entityToResult(userEntity);
    }

    /**
     * Flujo de solicitud de restablecimiento de contraseña:
     * 1. Valida el formato del email reutilizando la lógica del dominio.
     * 2. Busca el usuario; lanza NotFoundException si no existe.
     * 3. Genera contraseña temporal segura de {@value TEMPORARY_PASSWORD_LENGTH} caracteres.
     * 4. La hashea con BCrypt y la persiste junto con la expiración y el flag mustChangePassword.
     * 5. Envía el correo al usuario con la contraseña temporal en texto plano.
     */
    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        // 1. Valida formato de email — lanza InvalidEmailException si es inválido
        User.validateEmailFormat(email);

        // 2. Buscar usuario en base de datos — si no existe, responder con mensaje genérico
        //    para no filtrar información sobre qué correos están registrados (SFR-001).
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity =
                userProviderPort.findByEmail(email)
                        .orElseThrow(() -> new InvalidEmailException(
                                "Los datos ingresados son incorrectos."
                        ));

        // 3. Generar contraseña temporal segura
        String temporaryPassword = generateSecureTemporaryPassword();

        // 4. Hashear y persistir
        String hashedTemporaryPassword = passwordEncryptPort.encryptPassword(temporaryPassword);
        userEntity.setTemporaryPassword(hashedTemporaryPassword);
        userEntity.setTemporaryPasswordExpiration(
                LocalDateTime.now().plusMinutes(TEMPORARY_PASSWORD_EXPIRATION_MINUTES)
        );
        userEntity.setMustChangePassword(true);
        invalidateTokens(userEntity);      // Invalida JWTs anteriores del usuario
        userProviderPort.save(userEntity);

        // 5. Enviar correo con contraseña temporal en texto plano
        emailNotificationPort.sendTemporaryPasswordEmail(email, temporaryPassword);
    }

    /**
     * Flujo de actualización de contraseña definitiva tras login temporal:
     * 1. Valida el formato de la nueva contraseña reutilizando la lógica del dominio.
     * 2. Busca el usuario; lanza NotFoundException si no existe.
     * 3. Hashea la nueva contraseña y la persiste como contraseña principal.
     * 4. Limpia los campos de contraseña temporal e indicador mustChangePassword.
     */
    @Override
    @Transactional
    public void updatePassword(String email, String newPassword) {
        // 1. Valida formato de nueva contraseña — lanza InvalidPasswordException si es inválida
        User.validateEmailFormat(email);
        User.validatePasswordFormat(newPassword);

        // 2. Buscar usuario en base de datos
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity =
                userProviderPort.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException(
                                "No se encontró ningún usuario con el correo '" + email + "'."
                        ));

        // 3. Hashear nueva contraseña y actualizar contraseña principal
        String newHashedPassword = passwordEncryptPort.encryptPassword(newPassword);
        userEntity.setPassword(newHashedPassword);

        // 4. Invalidar contraseña temporal y limpiar indicadores
        userEntity.setTemporaryPassword(null);
        userEntity.setTemporaryPasswordExpiration(null);
        userEntity.setMustChangePassword(false);
        invalidateTokens(userEntity);      // Invalida JWTs anteriores del usuario

        userProviderPort.save(userEntity);
    }

    /**
     * Cambia la contraseña de un usuario autenticado desde su panel de usuario.
     *
     * <p>Flujo de validaciones:
     * <ol>
     *   <li>Valida complejidad de la nueva contraseña (dominio).</li>
     *   <li>Verifica que confirmación coincide con la nueva contraseña.</li>
     *   <li>Busca el usuario en base de datos.</li>
     *   <li>Verifica que la contraseña actual proporcionada coincide con la almacenada.</li>
     *   <li>Verifica que la nueva contraseña sea distinta a la actual.</li>
     *   <li>Hashea y persiste la nueva contraseña; limpia estados temporales si existieran.</li>
     * </ol>
     */
    @Override
    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {

        // 1. Validar complejidad de la nueva contraseña
        User.validatePasswordFormat(newPassword);

        // 2. Validar que confirmación coincide con la nueva contraseña
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordConfirmationMismatchException(
                    "La confirmación de contraseña no coincide con la nueva contraseña."
            );
        }

        // 3. Buscar usuario en base de datos
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity =
                userProviderPort.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException(
                                "No se encontró ningún usuario con el correo '" + email + "'."
                        ));

        // 4. Verificar que la contraseña actual proporcionada es correcta
        if (!passwordEncryptPort.matches(currentPassword, userEntity.getPassword())) {
            throw new InvalidCurrentPasswordException(
                    "La contraseña actual proporcionada es incorrecta."
            );
        }

        // 5. Verificar que la nueva contraseña sea distinta a la actual
        if (passwordEncryptPort.matches(newPassword, userEntity.getPassword())) {
            throw new PasswordSameAsCurrentException(
                    "La nueva contraseña no puede ser igual a la contraseña actual."
            );
        }

        // 6. Hashear y persistir; limpiar cualquier estado de recuperación temporal
        userEntity.setPassword(passwordEncryptPort.encryptPassword(newPassword));
        userEntity.setTemporaryPassword(null);
        userEntity.setTemporaryPasswordExpiration(null);
        userEntity.setMustChangePassword(false);
        invalidateTokens(userEntity);      // Invalida JWTs anteriores del usuario
        userProviderPort.save(userEntity);
    }

    /**
     * Cierra la sesión del usuario autenticado invalidando todos sus JWT activos.
     *
     * <p>Incrementa {@code tokenVersion} en base de datos. El filtro JWT compara este
     * valor en cada request y rechaza tokens con versión anterior con HTTP 401.
     */
    @Override
    @Transactional
    public void logout(String email) {
        com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity =
                userProviderPort.findByEmail(email)
                        .orElseThrow(() -> new NotFoundException(
                                "No se encontró ningún usuario con el correo '" + email + "'."
                        ));
        invalidateTokens(userEntity);
        userProviderPort.save(userEntity);
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    /**
     * Incrementa {@code tokenVersion} del usuario, invalidando todos sus JWT activos.
     * Llamar antes de {@code userProviderPort.save()} en cualquier operación que deba
     * cerrar sesión: logout, cambio de contraseña, recuperación de contraseña.
     *
     * @param userEntity Entidad usuario a actualizar (se modifica en memoria; el llamador persiste).
     */
    private void invalidateTokens(
            com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User userEntity) {
        userEntity.setTokenVersion(userEntity.getTokenVersion() + 1);
    }

    private String generateSecureTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(TEMPORARY_PASSWORD_LENGTH);
        for (int i = 0; i < TEMPORARY_PASSWORD_LENGTH; i++) {
            int index = random.nextInt(TEMPORARY_PASSWORD_CHARS.length());
            sb.append(TEMPORARY_PASSWORD_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
