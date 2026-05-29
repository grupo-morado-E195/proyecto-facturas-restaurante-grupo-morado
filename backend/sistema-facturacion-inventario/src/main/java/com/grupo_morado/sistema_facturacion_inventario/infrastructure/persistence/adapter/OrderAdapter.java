package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.adapter;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.OrderDAO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository.OrderDetailDAO;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para el módulo de órdenes.
 * Implementa el puerto de salida OrderProviderPort delegando en OrderDAO y OrderDetailDAO.
 */
@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderProviderPort {

    private final OrderDAO orderDAO;
    private final OrderDetailDAO orderDetailDAO;

    @Override
    public Optional<Order> findById(Long id) {
        return orderDAO.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderDAO.save(order);
    }

    @Override
    public List<OrderDetail> findDetailsByOrderId(Long orderId) {
        return orderDetailDAO.findByOrderId(orderId);
    }

    @Override
    public Page<Order> findByStatus(OrderStatusEnum status, Pageable pageable) {
        // Enforzar ordenamiento por defecto por fecha de creación ascendente (más antigua primero)
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        if (status == null) {
            return orderDAO.findAll(sortedPageable);
        } else {
            return orderDAO.findByStatus(status, sortedPageable);
        }
    }

    @Override
    public List<OrderDetail> saveDetails(List<OrderDetail> details) {
        return orderDetailDAO.saveAll(details);
    }

    @Override
    public void deleteDetails(List<OrderDetail> details) {
        orderDetailDAO.deleteAll(details);
    }
}
