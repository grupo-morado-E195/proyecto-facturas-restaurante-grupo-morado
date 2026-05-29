package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;

import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de órdenes y sus detalles.
 * Abstrae el acceso a datos de la capa de aplicación.
 */
public interface OrderProviderPort {

    /**
     * Busca una orden por su identificador.
     *
     * @param id Identificador de la orden.
     * @return Orden encontrada, o {@link Optional#empty()} si no existe.
     */
    Optional<Order> findById(Long id);

    /**
     * Persiste una orden (creación o actualización).
     *
     * @param order Entidad de orden a guardar.
     * @return Orden persistida.
     */
    Order save(Order order);

    /**
     * Busca los detalles asociados a una orden por su identificador.
     *
     * @param orderId Identificador de la orden.
     * @return Lista de detalles de orden encontrados.
     */
    List<OrderDetail> findDetailsByOrderId(Long orderId);

    /**
     * Busca órdenes paginadas filtradas opcionalmente por su estado.
     *
     * @param status   Estado opcional por el cual filtrar.
     * @param pageable Configuración de paginación.
     * @return Página de órdenes encontradas.
     */
    Page<Order> findByStatus(OrderStatusEnum status, Pageable pageable);

    /**
     * Persiste los detalles de una orden en lote.
     *
     * @param details Lista de detalles de orden a persistir.
     * @return Lista de detalles de orden persistidos.
     */
    List<OrderDetail> saveDetails(List<OrderDetail> details);

    /**
     * Elimina los detalles de una orden en base de datos.
     *
     * @param details Lista de detalles de orden a eliminar.
     */
    void deleteDetails(List<OrderDetail> details);
}
