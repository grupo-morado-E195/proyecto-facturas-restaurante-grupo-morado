package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRegisterDTO(

        @NotBlank
        String name,

        @NotBlank
        String lastname,

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotNull
        Long roleID
) {}
