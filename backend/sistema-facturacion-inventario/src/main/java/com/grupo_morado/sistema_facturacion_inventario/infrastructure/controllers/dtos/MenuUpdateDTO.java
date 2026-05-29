package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la modificación de un menú.
 */
public record MenuUpdateDTO(
        @NotBlank(message = "El nombre del menú es obligatorio.")
        String name
) {}
