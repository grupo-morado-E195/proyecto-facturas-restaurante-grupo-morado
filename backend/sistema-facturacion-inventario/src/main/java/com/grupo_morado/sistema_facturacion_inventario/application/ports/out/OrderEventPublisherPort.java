package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

/**
 * Puerto de salida para la publicación de eventos de órdenes en tiempo real.
 * Se utiliza únicamente para indicar la necesidad de refrescar la tabla/listado de órdenes.
 */
public interface OrderEventPublisherPort {

    /**
     * Publica un evento ligero para notificar que los clientes deben refrescar su listado de órdenes.
     */
    void publishOrderRefreshEvent();
}
