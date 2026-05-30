package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA específico para consultas de informes sobre órdenes.
 * Separado de OrderDAO para mantener la cohesión y el principio de responsabilidad única.
 */
@Repository
public interface OrderReportRepository extends JpaRepository<Order, Long> {

    /**
     * Suma el total de todas las órdenes con el estado indicado cuya fecha de creación
     * coincide con el rango dado.
     *
     * @param start  Fecha inicio.
     * @param end    Fecha fin.
     * @param status Estado de la orden (p. ej. PAGADO).
     * @return Suma de totales, o {@link Optional#empty()} si no hay registros.
     */
    @Query("SELECT SUM(o.total) FROM Order o " +
           "WHERE o.createdAt >= :start AND o.createdAt <= :end " +
           "AND o.status = :status")
    Optional<BigDecimal> sumTotalByDateRangeAndStatus(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve una lista de arreglos [nombre, apellidos, totalVentas] agrupados por mesero
     * para todas las órdenes con el estado indicado en el rango de fecha dado.
     *
     * @param start  Fecha inicio.
     * @param end    Fecha fin.
     * @param status Estado de la orden.
     * @return Lista de Object[] con índices: [0]=name, [1]=lastname, [2]=total.
     */
    @Query("SELECT o.waiter.name, o.waiter.lastname, SUM(o.total) " +
           "FROM Order o " +
           "WHERE o.createdAt >= :start AND o.createdAt <= :end " +
           "AND o.status = :status " +
           "GROUP BY o.waiter.id, o.waiter.name, o.waiter.lastname")
    List<Object[]> findSalesByWaiterAndDateRange(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato más vendido (por cantidad) en el rango de fecha indicado,
     * considerando solo órdenes con el estado dado.
     *
     * @param start  Fecha inicio.
     * @param end    Fecha fin.
     * @param status Estado de la orden.
     * @return Nombre del plato más vendido, o {@link Optional#empty()} si no hay datos.
     */
    @Query("SELECT od.dish.name " +
           "FROM OrderDetail od " +
           "WHERE od.order.createdAt >= :start AND od.order.createdAt <= :end " +
           "AND od.order.status = :status " +
           "GROUP BY od.dish.id, od.dish.name " +
           "ORDER BY SUM(od.quantity) DESC " +
           "LIMIT 1")
    Optional<String> findMostSoldDishByDateRange(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato menos vendido (por cantidad) en el rango de fecha indicado,
     * considerando solo órdenes con el estado dado.
     *
     * @param start  Fecha inicio.
     * @param end    Fecha fin.
     * @param status Estado de la orden.
     * @return Nombre del plato menos vendido, o {@link Optional#empty()} si no hay datos.
     */
    @Query("SELECT od.dish.name " +
           "FROM OrderDetail od " +
           "WHERE od.order.createdAt >= :start AND od.order.createdAt <= :end " +
           "AND od.order.status = :status " +
           "GROUP BY od.dish.id, od.dish.name " +
           "ORDER BY SUM(od.quantity) ASC " +
           "LIMIT 1")
    Optional<String> findLeastSoldDishByDateRange(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") OrderStatusEnum status);

    /**
     * Verifica si existen órdenes con el estado indicado en el rango de fecha dado.
     *
     * @param start  Fecha inicio.
     * @param end    Fecha fin.
     * @param status Estado de la orden.
     * @return {@code true} si existe al menos una orden que cumpla el criterio.
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o " +
           "WHERE o.createdAt >= :start AND o.createdAt <= :end " +
           "AND o.status = :status")
    boolean existsByDateRangeAndStatus(
            @Param("start") Timestamp start,
            @Param("end") Timestamp end,
            @Param("status") OrderStatusEnum status);
}
