package com.grupo_morado.sistema_facturacion_inventario.application.ports.out;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;

/**
 * Puerto de salida para publicación de eventos de mesa en tiempo real (WebSocket).
 * Desacopla la capa de aplicación de la implementación de mensajería.
 */
public interface TableEventPublisherPort {

    /**
     * Publica un evento de mesa al topic WebSocket {@code /topic/mesas}.
     * El frontend suscrito refrescará su listado al recibir este evento.
     *
     * @param table DTO de la mesa afectada por el evento.
     */
    void publishTableEvent(TableResultDTO table);
}
