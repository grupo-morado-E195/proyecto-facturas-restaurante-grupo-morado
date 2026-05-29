package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.repository;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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
     * coincide con la fecha dada.
     *
     * @param date   Fecha sobre la que se filtra (comparada contra CAST(createdAt AS localdate)).
     * @param status Estado de la orden (p. ej. PAGADO).
     * @return Suma de totales, o {@link Optional#empty()} si no hay registros.
     */
    @Query("SELECT SUM(o.total) FROM Order o " +
           "WHERE CAST(o.createdAt AS localdate) = :date " +
           "AND o.status = :status")
    Optional<BigDecimal> sumTotalByDateAndStatus(
            @Param("date") LocalDate date,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve una lista de arreglos [nombre, apellidos, totalVentas] agrupados por mesero
     * para todas las órdenes con el estado indicado en la fecha dada.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Lista de Object[] con índices: [0]=name, [1]=lastname, [2]=total.
     */
    @Query("SELECT o.waiter.name, o.waiter.lastname, SUM(o.total) " +
           "FROM Order o " +
           "WHERE CAST(o.createdAt AS localdate) = :date " +
           "AND o.status = :status " +
           "GROUP BY o.waiter.id, o.waiter.name, o.waiter.lastname")
    List<Object[]> findSalesByWaiterAndDate(
            @Param("date") LocalDate date,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato más vendido (por cantidad) en la fecha indicada,
     * considerando solo órdenes con el estado dado.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Nombre del plato más vendido, o {@link Optional#empty()} si no hay datos.
     */
    @Query("SELECT od.dish.name " +
           "FROM OrderDetail od " +
           "WHERE CAST(od.order.createdAt AS localdate) = :date " +
           "AND od.order.status = :status " +
           "GROUP BY od.dish.id, od.dish.name " +
           "ORDER BY SUM(od.quantity) DESC " +
           "LIMIT 1")
    Optional<String> findMostSoldDishByDate(
            @Param("date") LocalDate date,
            @Param("status") OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato menos vendido (por cantidad) en la fecha indicada,
     * considerando solo órdenes con el estado dado.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Nombre del plato menos vendido, o {@link Optional#empty()} si no hay datos.
     */
    @Query("SELECT od.dish.name " +
           "FROM OrderDetail od " +
           "WHERE CAST(od.order.createdAt AS localdate) = :date " +
           "AND od.order.status = :status " +
           "GROUP BY od.dish.id, od.dish.name " +
           "ORDER BY SUM(od.quantity) ASC " +
           "LIMIT 1")
    Optional<String> findLeastSoldDishByDate(
            @Param("date") LocalDate date,
            @Param("status") OrderStatusEnum status);

    /**
     * Verifica si existen órdenes con el estado indicado en la fecha dada.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return {@code true} si existe al menos una orden que cumpla el criterio.
     */
    @Query("SELECT COUNT(o) > 0 FROM Order o " +
           "WHERE CAST(o.createdAt AS localdate) = :date " +
           "AND o.status = :status")
    boolean existsByDateAndStatus(
            @Param("date") LocalDate date,
            @Param("status") OrderStatusEnum status);
}
