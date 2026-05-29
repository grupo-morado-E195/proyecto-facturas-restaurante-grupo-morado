package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderDetailCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.OrderUseCase;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.EmptyOrderException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InsufficientStockException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidFieldException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeCancelledException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidOrderStatusTransitionException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableNotAvailableException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeModifiedException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que implementa la lógica de negocio para la gestión de órdenes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements OrderUseCase {

    private final OrderProviderPort orderProviderPort;
    private final DishProviderPort dishProviderPort;
    private final TableProviderPort tableProviderPort;
    private final UserProviderPort userProviderPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final DishEventPublisherPort dishEventPublisherPort;
    private final OrderMapper orderMapper;

    @Value("${app.tax.percentage:0.08}")
    private BigDecimal taxPercentage;

    private boolean restoreStock(List<OrderDetail> details) {
        boolean stockUpdated = false;
        if (details != null && !details.isEmpty()) {
            for (OrderDetail detail : details) {
                Dish dish = detail.getDish();
                if (dish != null && detail.getQuantity() != null) {
                    dish.setStock(dish.getStock() + detail.getQuantity());
                    dishProviderPort.save(dish);
                    stockUpdated = true;
                    log.info("Restaurado stock para el plato con ID: {} ({} unidades). Nuevo stock: {}", 
                            dish.getId(), detail.getQuantity(), dish.getStock());
                }
            }
        }
        return stockUpdated;
    }

    private List<OrderDetail> discountStock(List<OrderDetailCreateDTO> detailsDto) {
        List<OrderDetail> details = new ArrayList<>();
        for (OrderDetailCreateDTO detailDto : detailsDto) {
            Dish dish = dishProviderPort.findById(detailDto.platoId())
                    .orElseThrow(() -> new NotFoundException("El plato con id '" + detailDto.platoId() + "' no fue encontrado."));

            if (dish.getStatus() != StatusEnum.ACTIVO) {
                throw new InvalidFieldException("El plato con id '" + detailDto.platoId() + "' está inactivo.");
            }

            if (dish.getStock() < detailDto.cantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el plato '" + dish.getName() + "'. Stock disponible: " + dish.getStock() + ", cantidad solicitada: " + detailDto.cantidad());
            }

            dish.setStock(dish.getStock() - detailDto.cantidad());
            dishProviderPort.save(dish);

            OrderDetail detail = new OrderDetail();
            detail.setDish(dish);
            detail.setQuantity(detailDto.cantidad());
            detail.setPrice(dish.getPrice());
            detail.setObservation(detailDto.observaciones());

            details.add(detail);
        }
        return details;
    }

    @Override
    @Transactional
    public OrderResultDTO createOrder(OrderCreateDTO dto, String emailMesero) {
        log.info("Iniciando registro de nueva orden para mesa ID: {}, por mesero: {}", dto.mesaId(), emailMesero);

        if (dto.detalles() == null || dto.detalles().isEmpty()) {
            throw new EmptyOrderException("Los detalles de la orden no pueden estar vacíos.");
        }

        Table table = tableProviderPort.findById(dto.mesaId())
                .orElseThrow(() -> new NotFoundException("La mesa con id '" + dto.mesaId() + "' no fue encontrada."));

        if (table.getStatus() != StatusEnum.ACTIVO || table.getDisponibility() != DisponibilityStateEnum.DISPONIBLE) {
            throw new TableNotAvailableException("La mesa con número " + table.getNumber() + " no está activa o ya está ocupada.");
        }

        User waiter = userProviderPort.findByEmail(emailMesero)
                .orElseThrow(() -> new NotFoundException("El mesero con correo '" + emailMesero + "' no fue encontrado."));

        table.setDisponibility(DisponibilityStateEnum.OCUPADA);
        tableProviderPort.save(table);

        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDIENTE);
        order.setTable(table);
        order.setWaiter(waiter);

        List<OrderDetail> details = discountStock(dto.detalles());
        BigDecimal subtotalSub = BigDecimal.ZERO;
        for (OrderDetail detail : details) {
            detail.setOrder(order);
            BigDecimal itemTotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            subtotalSub = subtotalSub.add(itemTotal);
        }

        BigDecimal tax = subtotalSub.multiply(taxPercentage);
        BigDecimal total = subtotalSub.add(tax);

        order.setSubtotal(subtotalSub);
        order.setConsumptionTax(tax);
        order.setTotal(total);

        Order savedOrder = orderProviderPort.save(order);
        orderProviderPort.saveDetails(details);

        orderEventPublisherPort.publishOrderRefreshEvent();
        dishEventPublisherPort.publishDishRefreshEvent();

        log.info("Orden con ID: {} registrada exitosamente. Mesa ID: {}, Total: {}", savedOrder.getId(), table.getId(), total);

        return orderMapper.entityToResult(savedOrder);
    }

    @Override
    @Transactional
    public OrderResultDTO updateOrder(Long orderId, OrderUpdateDTO dto) {
        log.info("Iniciando modificación de la orden con ID: {}", orderId);

        if (dto.detalles() == null || dto.detalles().isEmpty()) {
            throw new EmptyOrderException("Los detalles de la orden no pueden estar vacíos.");
        }

        Order order = orderProviderPort.findById(orderId)
                .orElseThrow(() -> new NotFoundException("La orden con id '" + orderId + "' no fue encontrada."));

        if (order.getStatus() != OrderStatusEnum.PENDIENTE) {
            throw new OrderCannotBeModifiedException("Solo se puede modificar una orden si su estado es PENDIENTE.");
        }

        Table oldTable = order.getTable();
        if (oldTable == null || !oldTable.getId().equals(dto.mesaId())) {
            Table newTable = tableProviderPort.findById(dto.mesaId())
                    .orElseThrow(() -> new NotFoundException("La mesa con id '" + dto.mesaId() + "' no fue encontrada."));

            if (newTable.getStatus() != StatusEnum.ACTIVO || newTable.getDisponibility() != DisponibilityStateEnum.DISPONIBLE) {
                throw new TableNotAvailableException("La mesa con número " + newTable.getNumber() + " no está activa o ya está ocupada.");
            }

            if (oldTable != null) {
                oldTable.setDisponibility(DisponibilityStateEnum.DISPONIBLE);
                tableProviderPort.save(oldTable);
            }

            newTable.setDisponibility(DisponibilityStateEnum.OCUPADA);
            tableProviderPort.save(newTable);

            order.setTable(newTable);
        }

        List<OrderDetail> oldDetails = orderProviderPort.findDetailsByOrderId(orderId);
        boolean stockRestored = restoreStock(oldDetails);

        orderProviderPort.deleteDetails(oldDetails);

        List<OrderDetail> newDetails = discountStock(dto.detalles());
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderDetail detail : newDetails) {
            detail.setOrder(order);
            BigDecimal itemTotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal tax = subtotal.multiply(taxPercentage);
        BigDecimal total = subtotal.add(tax);

        order.setSubtotal(subtotal);
        order.setConsumptionTax(tax);
        order.setTotal(total);

        orderProviderPort.save(order);
        orderProviderPort.saveDetails(newDetails);

        orderEventPublisherPort.publishOrderRefreshEvent();
        if (stockRestored || !newDetails.isEmpty()) {
            dishEventPublisherPort.publishDishRefreshEvent();
        }

        log.info("Orden con ID: {} modificada exitosamente. Total: {}", orderId, total);

        return orderMapper.entityToResult(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("Iniciando cancelación de la orden con ID: {}", orderId);

        Order order = orderProviderPort.findById(orderId)
                .orElseThrow(() -> new NotFoundException("La orden con id '" + orderId + "' no fue encontrada."));

        if (order.getStatus() != OrderStatusEnum.PENDIENTE) {
            throw new OrderCannotBeCancelledException("Solo se puede cancelar una orden si su estado es PENDIENTE.");
        }

        order.setStatus(OrderStatusEnum.CANCELADO);
        orderProviderPort.save(order);

        List<OrderDetail> details = orderProviderPort.findDetailsByOrderId(orderId);
        boolean stockUpdated = restoreStock(details);

        Table table = order.getTable();
        if (table != null) {
            table.setDisponibility(DisponibilityStateEnum.DISPONIBLE);
            tableProviderPort.save(table);
            log.info("Mesa con número: {} liberada y establecida en estado DISPONIBLE.", table.getNumber());
        }

        orderEventPublisherPort.publishOrderRefreshEvent();
        if (stockUpdated) {
            dishEventPublisherPort.publishDishRefreshEvent();
        }

        log.info("Orden con ID: {} cancelada exitosamente y recursos restablecidos.", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResultDTO getOrderById(Long id) {
        log.info("Consultando la orden con ID: {}", id);

        // Buscar orden existente. Lanzar NotFoundException si no existe.
        Order order = orderProviderPort.findById(id)
                .orElseThrow(() -> new NotFoundException("La orden con id '" + id + "' no fue encontrada."));

        return orderMapper.entityToResult(order);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO> getOrders(
            OrderStatusEnum status,
            org.springframework.data.domain.Pageable pageable
    ) {
        log.info("Consultando lista paginada de órdenes con estado: {}", status);
        org.springframework.data.domain.Page<Order> ordersPage = orderProviderPort.findByStatus(status, pageable);
        return ordersPage.map(orderMapper::entityToSummary);
    }

    private void validateStatusTransition(OrderStatusEnum current, OrderStatusEnum next) {
        if (current == OrderStatusEnum.PENDIENTE && next == OrderStatusEnum.EN_PREPARACION) {
            return;
        }
        if (current == OrderStatusEnum.EN_PREPARACION && next == OrderStatusEnum.LISTO) {
            return;
        }
        throw new InvalidOrderStatusTransitionException(
                "No se permite cambiar el estado de la orden de '" + current + "' a '" + next + "'."
        );
    }

    @Override
    @Transactional
    public OrderResultDTO updateOrderStatus(Long orderId, OrderStatusEnum nuevoEstado) {
        log.info("Iniciando actualización de estado para la orden con ID: {} a nuevo estado: {}", orderId, nuevoEstado);

        // 1. Buscar orden existente. Lanzar NotFoundException si no existe.
        Order order = orderProviderPort.findById(orderId)
                .orElseThrow(() -> new NotFoundException("La orden con id '" + orderId + "' no fue encontrada."));

        // 2. Validar transición de estado.
        validateStatusTransition(order.getStatus(), nuevoEstado);

        // 3. Cambiar y persistir estado.
        order.setStatus(nuevoEstado);
        orderProviderPort.save(order);

        // 4. Emitir evento WebSocket de actualización.
        orderEventPublisherPort.publishOrderRefreshEvent();

        log.info("Estado de la orden con ID: {} actualizado exitosamente a: {}", orderId, nuevoEstado);

        // 5. Retornar OrderResultDTO actualizado.
        return orderMapper.entityToResult(order);
    }
}
