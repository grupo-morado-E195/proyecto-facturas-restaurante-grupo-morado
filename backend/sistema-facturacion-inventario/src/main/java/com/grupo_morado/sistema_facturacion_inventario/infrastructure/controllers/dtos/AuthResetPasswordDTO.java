package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el endpoint de solicitud de restablecimiento de contraseña.
 *
 * @param email Correo electrónico del usuario que solicita el restablecimiento.
 */
public record AuthResetPasswordDTO(

        @NotBlank(message = "El correo electrónico es obligatorio")
        String email

) {}
