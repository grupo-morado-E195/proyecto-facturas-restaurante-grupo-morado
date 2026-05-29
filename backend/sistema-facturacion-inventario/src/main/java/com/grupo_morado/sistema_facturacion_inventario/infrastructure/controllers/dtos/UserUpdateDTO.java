package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para modificar nombre, apellidos y rol de un usuario (SFR-008).
 */
public record UserUpdateDTO(

        @NotBlank(message = "El nombre es obligatorio.")
        String name,

        @NotBlank(message = "Los apellidos son obligatorios.")
        String lastname,

        @NotNull(message = "El rol es obligatorio.")
        Long roleId,

        String password
) {}
