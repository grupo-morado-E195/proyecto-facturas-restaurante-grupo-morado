package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para las consultas de informes de ventas.
 * Desacopla la capa de aplicación de la implementación JPA concreta.
 */
public interface ReportProviderPort {

    /**
     * Suma el total de todas las órdenes con el estado indicado en la fecha dada.
     *
     * @param date   Fecha sobre la que se filtra.
     * @param status Estado de la orden.
     * @return Suma de totales, o {@link Optional#empty()} si no hay registros.
     */
    Optional<BigDecimal> sumTotalByDateAndStatus(LocalDate date, OrderStatusEnum status);

    /**
     * Devuelve las ventas agrupadas por mesero para la fecha y estado indicados.
     * Cada elemento del array contiene: [0]=nombre, [1]=apellidos, [2]=total.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Lista de Object[] con los datos de ventas por mesero.
     */
    List<Object[]> findSalesByWaiterAndDate(LocalDate date, OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato más vendido en la fecha indicada.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Nombre del plato más vendido, o {@link Optional#empty()} si no hay datos.
     */
    Optional<String> findMostSoldDishByDate(LocalDate date, OrderStatusEnum status);

    /**
     * Devuelve el nombre del plato menos vendido en la fecha indicada.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return Nombre del plato menos vendido, o {@link Optional#empty()} si no hay datos.
     */
    Optional<String> findLeastSoldDishByDate(LocalDate date, OrderStatusEnum status);

    /**
     * Verifica si existen órdenes con el estado indicado en la fecha dada.
     *
     * @param date   Fecha de filtrado.
     * @param status Estado de la orden.
     * @return {@code true} si existe al menos una orden que cumpla el criterio.
     */
    boolean existsByDateAndStatus(LocalDate date, OrderStatusEnum status);
}
