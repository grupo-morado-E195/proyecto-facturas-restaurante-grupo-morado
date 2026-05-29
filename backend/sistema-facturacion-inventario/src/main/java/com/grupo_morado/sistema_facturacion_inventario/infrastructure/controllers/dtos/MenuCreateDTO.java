package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la creación de un menú.
 */
public record MenuCreateDTO(
        @NotBlank(message = "El nombre del menú es obligatorio.")
        String name
) {}
