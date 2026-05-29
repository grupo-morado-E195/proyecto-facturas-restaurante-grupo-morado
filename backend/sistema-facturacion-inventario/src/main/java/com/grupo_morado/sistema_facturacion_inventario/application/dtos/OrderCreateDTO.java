package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO para la creación de una orden.
 * Contiene la mesa asignada y la lista de platos con sus detalles.
 */
public record OrderCreateDTO(
        @NotNull(message = "La mesa es obligatoria.")
        Long mesaId,

        @NotEmpty(message = "Los detalles de la orden no pueden estar vacíos.")
        List<OrderDetailCreateDTO> detalles
) {}
