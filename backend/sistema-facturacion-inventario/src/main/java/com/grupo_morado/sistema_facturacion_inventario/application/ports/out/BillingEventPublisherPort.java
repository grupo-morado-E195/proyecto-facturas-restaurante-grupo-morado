package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

/**
 * Puerto de salida para la publicación de eventos de facturación en tiempo real.
 * Se utiliza para indicar que se debe refrescar la información en el frontend de órdenes y facturación.
 */
public interface BillingEventPublisherPort {

    /**
     * Publica un evento ligero para notificar que los clientes deben refrescar su listado de órdenes y facturación.
     */
    void publishBillingRefreshEvent();
}
