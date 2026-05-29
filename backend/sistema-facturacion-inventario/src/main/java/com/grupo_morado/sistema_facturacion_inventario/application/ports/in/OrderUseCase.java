package com.grupo_morado.sistema_facturacion_inventario.application.ports.in;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderUpdateDTO;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para el módulo de Órdenes.
 * Define las operaciones disponibles sobre las órdenes del restaurante.
 */
public interface OrderUseCase {

    /**
     * Registra una nueva orden en el sistema.
     * Realiza validaciones de mesa, plato, stock y realiza los cálculos
     * financieros correspondientes.
     *
     * @param dto          DTO con la información de la orden a crear.
     * @param emailMesero  Correo electrónico del mesero que crea la orden.
     * @return DTO con la información detallada de la orden creada.
     */
    OrderResultDTO createOrder(OrderCreateDTO dto, String emailMesero);

    /**
     * Modifica una orden existente en estado PENDIENTE.
     * Permite cambiar la mesa y actualiza completamente los detalles, stock y totales.
     *
     * @param orderId Identificador de la orden a modificar.
     * @param dto     DTO con los nuevos datos de la orden.
     * @return DTO con la información detallada de la orden modificada.
     */
    OrderResultDTO updateOrder(Long orderId, OrderUpdateDTO dto);

    /**
     * Cancela una orden existente si su estado es PENDIENTE.
     * Restaura automáticamente el stock de los platos asociados
     * y libera la mesa asociada (vuelve a estado DISPONIBLE).
     *
     * @param orderId Identificador de la orden a cancelar.
     */
    void cancelOrder(Long orderId);

    /**
     * Muestra la información detallada de una orden para el modal.
     *
     * @param id Identificador de la orden.
     * @return DTO con la información detallada de la orden.
     */
    OrderResultDTO getOrderById(Long id);

    /**
     * Obtiene una lista paginada de órdenes filtradas opcionalmente por estado.
     * Las órdenes se ordenan de manera predeterminada por fecha de creación ascendente.
     *
     * @param status   Estado opcional por el cual filtrar.
     * @param pageable Configuración de paginación.
     * @return Página con los resúmenes de las órdenes encontradas.
     */
    Page<OrderSummaryResultDTO> getOrders(OrderStatusEnum status, Pageable pageable);

    /**
     * Actualiza el estado de una orden.
     * Los flujos permitidos son de PENDIENTE a EN_PREPARACION, y de EN_PREPARACION a LISTO.
     *
     * @param orderId     Identificador de la orden.
     * @param nuevoEstado Nuevo estado a establecer.
     * @return DTO con la información detallada de la orden actualizada.
     */
    OrderResultDTO updateOrderStatus(Long orderId, OrderStatusEnum nuevoEstado);
}
