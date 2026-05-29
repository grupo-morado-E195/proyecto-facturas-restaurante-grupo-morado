package com.grupo_morado.sistema_facturacion_inventario.application.dtos;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de resultado para las operaciones del módulo de Órdenes.
 * Retorna todos los campos necesarios para mostrar los detalles de la orden en el modal.
 *
 * @param id             Identificador único de la orden.
 * @param tableNumber    Número de mesa asociada a la orden.
 * @param details        Listado de platos con cantidad, observaciones y precios.
 * @param status         Estado actual de la orden.
 * @param subtotal       Subtotal de la orden.
 * @param consumptionTax Valor del impuesto al consumo.
 * @param total          Valor total a pagar.
 */
public record OrderResultDTO(
        Long id,
        Integer tableNumber,
        List<OrderDetailResultDTO> details,
        OrderStatusEnum status,
        BigDecimal subtotal,
        BigDecimal consumptionTax,
        BigDecimal total
) {}
