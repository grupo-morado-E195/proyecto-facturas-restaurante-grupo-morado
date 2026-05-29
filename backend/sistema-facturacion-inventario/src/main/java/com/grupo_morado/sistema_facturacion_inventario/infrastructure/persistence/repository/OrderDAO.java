package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Order (Orden).
 */
@Repository
public interface OrderDAO extends JpaRepository<Order, Long> {

    /**
     * Consulta derivada para buscar órdenes filtradas por estado de manera paginada.
     *
     * @param status   Estado de la orden.
     * @param pageable Configuración de paginación.
     * @return Página de órdenes.
     */
    Page<Order> findByStatus(OrderStatusEnum status, Pageable pageable);
}
