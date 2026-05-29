package com.grupo_morado.sistema_facturacion_inventario.infrastructure.websocket;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para la publicación de eventos en tiempo real sobre las órdenes.
 * Implementa OrderEventPublisherPort usando SimpMessagingTemplate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishOrderRefreshEvent() {
        log.info("Publicando evento en tiempo real para refrescar listado de órdenes.");
        messagingTemplate.convertAndSend("/topic/ordenes", "refresh");
    }
}
