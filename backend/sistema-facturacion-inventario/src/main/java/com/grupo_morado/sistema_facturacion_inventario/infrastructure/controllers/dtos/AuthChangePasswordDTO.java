package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint de cambio de contraseña autenticado
 * ({@code POST /api/auth/change-password}).
 *
 * <p>Se requiere la contraseña actual para verificar la identidad del usuario
 * antes de actualizar la contraseña. El email se extrae del token JWT,
 * nunca del cuerpo de la petición.
 *
 * @param currentPassword Contraseña actual del usuario en texto plano.
 * @param newPassword     Nueva contraseña elegida por el usuario.
 * @param confirmPassword Confirmación de la nueva contraseña (debe coincidir exactamente).
 */
public record AuthChangePasswordDTO(

        @NotBlank(message = "La contraseña actual es obligatoria")
        String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria")
        String newPassword,

        @NotBlank(message = "La confirmación de contraseña es obligatoria")
        String confirmPassword

) {}
