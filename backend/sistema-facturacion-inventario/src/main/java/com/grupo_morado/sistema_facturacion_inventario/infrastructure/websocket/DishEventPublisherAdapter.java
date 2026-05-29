package com.grupo_morado.sistema_facturacion_inventario.infrastructure.websocket;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para la publicación de eventos en tiempo real sobre los platos.
 * Implementa DishEventPublisherPort usando SimpMessagingTemplate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DishEventPublisherAdapter implements DishEventPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishDishRefreshEvent() {
        log.info("Publicando evento en tiempo real para refrescar listado de platos.");
        // Envía una simple señal de refresco en lugar de objetos completos
        messagingTemplate.convertAndSend("/topic/platos", "refresh");
    }
}
