package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de un detalle de orden.
 * Contiene el plato a ordenar, la cantidad y posibles observaciones.
 */
public record OrderDetailCreateDTO(
        @NotNull(message = "El id del plato es obligatorio.")
        Long platoId,

        @NotNull(message = "La cantidad es obligatoria.")
        @Min(value = 1, message = "La cantidad mínima debe ser 1.")
        Integer cantidad,

        String observaciones
) {}
