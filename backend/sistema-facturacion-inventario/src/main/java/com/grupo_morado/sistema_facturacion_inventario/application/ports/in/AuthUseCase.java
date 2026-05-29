package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;

public interface AuthUseCase {

    AuthLoginResultDTO login(User user);
    AuthRegisterResultDTO register(AuthRegisterDTO user);
    void requestPasswordReset(String email);

    /**
     * Actualiza la contraseña definitiva del usuario tras un login con contraseña temporal.
     * No requiere la contraseña actual — se usa cuando el sistema ya autenticó al usuario
     * con la contraseña temporal.
     *
     * @param email       Email del usuario autenticado (extraído del JWT).
     * @param newPassword Nueva contraseña definitiva en texto plano.
     */
    void updatePassword(String email, String newPassword);

    /**
     * Cambia la contraseña de un usuario autenticado desde su panel de usuario.
     * Requiere la contraseña actual para verificar la identidad.
     *
     * @param email           Email del usuario autenticado (extraído del JWT).
     * @param currentPassword Contraseña actual en texto plano.
     * @param newPassword     Nueva contraseña en texto plano.
     * @param confirmPassword Confirmación de la nueva contraseña.
     */
    void changePassword(String email, String currentPassword, String newPassword, String confirmPassword);

    /**
     * Cierra la sesión del usuario autenticado invalidando todos sus JWT activos.
     *
     * <p>Mecanismo: incrementa el campo {@code tokenVersion} del usuario en base de datos.
     * El filtro JWT compara este valor en cada request; si no coincide con el claim del token,
     * la petición se rechaza con 401, forzando una nueva autenticación.
     *
     * @param email Email del usuario autenticado (extraído del JWT).
     */
    void logout(String email);
}

