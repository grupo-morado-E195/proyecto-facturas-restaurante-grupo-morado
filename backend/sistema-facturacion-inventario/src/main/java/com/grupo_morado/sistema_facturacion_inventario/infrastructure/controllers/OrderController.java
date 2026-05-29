package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.OrderStatusUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

/**
 * Controlador REST para el módulo de Órdenes.
 * Expone endpoints para las operaciones sobre las órdenes del restaurante.
 */
@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * Registra una nueva orden en el sistema.
     * Endpoint restringido a MESERO.
     *
     * @param orderCreateDTO DTO con la información de la orden.
     * @param userDetails    Usuario autenticado (mesero).
     * @return DTO con la información detallada de la orden creada.
     */
    @PostMapping
    public ResponseEntity<OrderResultDTO> createOrder(
            @Valid @RequestBody OrderCreateDTO orderCreateDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("REST request para registrar una nueva orden por el mesero: {}.", userDetails.getUsername());
        OrderResultDTO result = orderService.createOrder(orderCreateDTO, userDetails.getUsername());
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(result);
    }

    /**
     * Modifica una orden existente en estado PENDIENTE.
     * Endpoint restringido a MESERO.
     *
     * @param id             Identificador de la orden a modificar.
     * @param orderUpdateDTO DTO con la nueva información de la orden.
     * @return DTO con la información detallada de la orden modificada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResultDTO> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderUpdateDTO orderUpdateDTO
    ) {
        log.info("REST request para modificar la orden con ID: {}.", id);
        OrderResultDTO result = orderService.updateOrder(id, orderUpdateDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Cancela una orden existente.
     * Endpoint restringido a MESERO y ADMINISTRADOR.
     *
     * @param id Identificador de la orden a cancelar.
     * @return Mensaje de confirmación del éxito de la operación.
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id) {
        log.info("REST request para cancelar la orden con ID: {}.", id);
        orderService.cancelOrder(id);
        return ResponseEntity.ok(Map.of("message", "Orden cancelada correctamente."));
    }

    /**
     * Muestra el detalle de una orden en el modal.
     * Endpoint accesible para MESERO y CHEF.
     *
     * @param id Identificador de la orden a mostrar.
     * @return DTO con los detalles de la orden.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResultDTO> getOrderById(@PathVariable Long id) {
        log.info("REST request para obtener el detalle de la orden con ID: {}.", id);
        OrderResultDTO result = orderService.getOrderById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Lista todas las órdenes registradas de forma paginada, con filtro opcional por estado.
     * Ordenadas automáticamente por fecha de creación ascendente (más antigua primero).
     * Endpoint accesible para ADMINISTRADOR, MESERO y CHEF.
     *
     * @param status   Estado opcional por el cual filtrar.
     * @param pageable Configuración de paginación.
     * @return Página de resúmenes de órdenes.
     */
    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO>> getOrders(
            @org.springframework.web.bind.annotation.RequestParam(required = false) com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum status,
            org.springframework.data.domain.Pageable pageable
    ) {
        log.info("REST request para listar todas las órdenes paginadas con estado: {}.", status);
        org.springframework.data.domain.Page<com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO> result = orderService.getOrders(status, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Actualiza el estado de una orden.
     * Endpoint restringido a CHEF.
     *
     * @param id               Identificador de la orden.
     * @param statusUpdateDTO DTO con el nuevo estado.
     * @return DTO con la información detallada de la orden actualizada.
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<OrderResultDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdateDTO
    ) {
        log.info("REST request para actualizar el estado de la orden con ID: {} a nuevo estado: {}.", id, statusUpdateDTO.nuevoEstado());
        OrderResultDTO result = orderService.updateOrderStatus(id, statusUpdateDTO.nuevoEstado());
        return ResponseEntity.ok(result);
    }
}
