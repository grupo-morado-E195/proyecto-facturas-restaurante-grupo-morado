package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

/**
 * Puerto de salida para la publicación de eventos de platos en tiempo real.
 * Se utiliza únicamente para indicar la necesidad de refrescar la tabla/listado de platos.
 */
public interface DishEventPublisherPort {

    /**
     * Publica un evento ligero para notificar que los clientes deben refrescar su listado de platos.
     */
    void publishDishRefreshEvent();
}
