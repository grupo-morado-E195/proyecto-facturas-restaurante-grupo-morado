package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para crear un usuario desde el módulo de Gestión de Usuarios (SFR-008).
 * El sistema asigna un email generado y una contraseña temporal enviada por correo.
 */
public record UserCreateDTO(

        @NotBlank(message = "El nombre es obligatorio.")
        String name,

        @NotBlank(message = "Los apellidos son obligatorios.")
        String lastname,

        @NotBlank(message = "El correo electrónico es obligatorio.")
        String email,

        @NotNull(message = "El rol es obligatorio.")
        Long roleId,

        @NotBlank(message = "La contraseña es obligatoria.")
        String password
) {}
