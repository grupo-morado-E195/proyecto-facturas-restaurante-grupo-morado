package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthLoginResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.AuthRegisterResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.models.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.AuthRegisterDTO;

public interface AuthUseCase {

    AuthLoginResultDTO login(User user);
    AuthRegisterResultDTO register(AuthRegisterDTO user);

    /**
     * Solicita un restablecimiento de contraseña para el email dado.
     * Genera una contraseña temporal, la persiste hasheada y la envía por correo.
     *
     * @param email Correo electrónico del usuario.
     */
    void requestPasswordReset(String email);

    /**
     * Actualiza la contraseña definitiva del usuario tras un login con contraseña temporal.
     * Invalida la contraseña temporal y limpia el indicador mustChangePassword.
     *
     * @param email       Correo del usuario autenticado con contraseña temporal.
     * @param newPassword Nueva contraseña definitiva en texto plano.
     */
    void updatePassword(String email, String newPassword);
}

