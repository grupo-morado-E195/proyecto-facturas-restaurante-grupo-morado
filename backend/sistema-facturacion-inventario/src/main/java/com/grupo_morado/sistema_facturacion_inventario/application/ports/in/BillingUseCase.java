package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;

/**
 * Puerto de entrada para el módulo de Facturación.
 * Define las operaciones disponibles para facturar las órdenes.
 */
public interface BillingUseCase {

    /**
     * Factura una orden existente si su estado es LISTO (lista).
     * Cambia el estado de la orden a PAGADO (facturada)
     * y libera la mesa asociada (vuelve a estado DISPONIBLE).
     *
     * @param orderId Identificador de la orden a facturar.
     * @return DTO con la información detallada de la orden facturada.
     */
    OrderResultDTO invoiceOrder(Long orderId);
}
