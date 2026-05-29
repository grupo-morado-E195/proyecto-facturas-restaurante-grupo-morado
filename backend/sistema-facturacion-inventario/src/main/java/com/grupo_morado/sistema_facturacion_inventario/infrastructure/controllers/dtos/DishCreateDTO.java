package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para la creación/registro de un plato.
 */
public record DishCreateDTO(
        @NotBlank(message = "El nombre del plato es obligatorio.")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio.")
        @DecimalMin(value = "0.0", message = "El precio no puede ser negativo.")
        BigDecimal price,

        @Min(value = 0, message = "El stock inicial no puede ser negativo.")
        Integer stock,

        @NotNull(message = "El menú asociado es obligatorio.")
        Long menuId
) {}
