package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.BillingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el módulo de Facturación.
 * Expone endpoints para facturar las órdenes de compra del restaurante.
 */
@RestController
@RequestMapping("/api/facturacion")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingUseCase billingUseCase;

    /**
     * Factura una orden de compra existente.
     * Endpoint restringido a CAJERO.
     *
     * @param id Identificador de la orden a facturar.
     * @return DTO con la información detallada de la orden facturada.
     */
    @PutMapping("/ordenes/{id}/facturar")
    public ResponseEntity<OrderResultDTO> invoiceOrder(@PathVariable Long id) {
        log.info("REST request para facturar la orden con ID: {}.", id);
        OrderResultDTO result = billingUseCase.invoiceOrder(id);
        return ResponseEntity.ok(result);
    }
}
