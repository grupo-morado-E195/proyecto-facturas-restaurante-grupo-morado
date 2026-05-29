package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import java.math.BigDecimal;

/**
 * DTO para representar el detalle de una orden en el modal.
 *
 * @param nombrePlato      Nombre del plato.
 * @param cantidad         Cantidad ordenada.
 * @param observaciones    Observaciones/notas para el plato.
 * @param precioUnitario   Precio unitario del plato.
 * @param subtotalDetalle  Subtotal calculado para esta línea de detalle (precioUnitario * cantidad).
 */
public record OrderDetailResultDTO(
        String nombrePlato,
        Integer cantidad,
        String observaciones,
        BigDecimal precioUnitario,
        BigDecimal subtotalDetalle
) {}
