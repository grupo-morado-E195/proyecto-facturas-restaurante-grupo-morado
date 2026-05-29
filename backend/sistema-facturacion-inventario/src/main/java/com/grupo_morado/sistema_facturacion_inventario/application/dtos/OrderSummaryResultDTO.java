package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;

import java.sql.Timestamp;

/**
 * DTO para representar el resumen de una orden en el listado/tabla.
 */
public record OrderSummaryResultDTO(
        Long id,
        Integer numeroMesa,
        OrderStatusEnum estado,
        String nombreMesero,
        Timestamp fechaCreacion
) {
}
