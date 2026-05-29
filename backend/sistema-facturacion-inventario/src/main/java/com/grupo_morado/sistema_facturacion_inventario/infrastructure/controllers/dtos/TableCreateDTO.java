package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de una mesa.
 */
public record TableCreateDTO(
        @NotNull(message = "El número de mesa es obligatorio.")
        @Min(value = 0, message = "El número de mesa no puede ser negativo.")
        Integer number
) {}
