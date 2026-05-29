package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO para la modificación de una orden existente.
 * Permite cambiar la mesa y proporciona una nueva lista completa de detalles.
 */
public record OrderUpdateDTO(
        @NotNull(message = "La mesa es obligatoria.")
        Long mesaId,

        @NotEmpty(message = "Los detalles de la orden no pueden estar vacíos.")
        List<OrderDetailCreateDTO> detalles
) {}
