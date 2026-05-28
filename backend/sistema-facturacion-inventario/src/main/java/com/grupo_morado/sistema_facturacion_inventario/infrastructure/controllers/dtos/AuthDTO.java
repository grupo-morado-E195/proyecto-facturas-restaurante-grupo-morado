package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthDTO(

        @NotBlank
        String email,

        @NotBlank
        String password
) {}
