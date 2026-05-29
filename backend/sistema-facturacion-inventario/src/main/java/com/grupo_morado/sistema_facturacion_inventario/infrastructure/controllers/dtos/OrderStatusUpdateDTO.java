package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el estado de una orden.
 */
public record OrderStatusUpdateDTO(
        @NotNull(message = "El nuevo estado es obligatorio.")
        OrderStatusEnum nuevoEstado
) {
}
