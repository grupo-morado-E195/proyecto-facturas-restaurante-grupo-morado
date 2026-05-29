package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.BillingUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.BillingEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeInvoicedException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa la lógica de negocio para la facturación de órdenes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService implements BillingUseCase {

    private final OrderProviderPort orderProviderPort;
    private final TableProviderPort tableProviderPort;
    private final BillingEventPublisherPort billingEventPublisherPort;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResultDTO invoiceOrder(Long orderId) {
        log.info("Iniciando facturación de la orden con ID: {}", orderId);

        // 1. Buscar orden existente. Lanzar NotFoundException si no existe.
        Order order = orderProviderPort.findById(orderId)
                .orElseThrow(() -> new NotFoundException("La orden con id '" + orderId + "' no fue encontrada."));

        // 2. Validar que la orden esté en estado LISTO (lista).
        if (order.getStatus() != OrderStatusEnum.LISTO) {
            throw new OrderCannotBeInvoicedException("Solo se puede facturar una orden si su estado es LISTA.");
        }

        // 3. Cambiar el estado de la orden a PAGADO (facturada).
        order.setStatus(OrderStatusEnum.PAGADO);
        orderProviderPort.save(order);

        // 4. Liberar la mesa asociada cambiando disponibilidad a DISPONIBLE (libre).
        Table table = order.getTable();
        if (table != null) {
            table.setDisponibility(DisponibilityStateEnum.DISPONIBLE);
            tableProviderPort.save(table);
            log.info("Mesa con número: {} liberada y establecida en estado DISPONIBLE.", table.getNumber());
        }

        // 5. Emitir eventos WebSocket para refrescar órdenes y facturación en tiempo real.
        billingEventPublisherPort.publishBillingRefreshEvent();

        log.info("Orden con ID: {} facturada exitosamente y mesa liberada.", orderId);

        // 6. Retornar DTO de la orden facturada
        return orderMapper.entityToResult(order);
    }
}
