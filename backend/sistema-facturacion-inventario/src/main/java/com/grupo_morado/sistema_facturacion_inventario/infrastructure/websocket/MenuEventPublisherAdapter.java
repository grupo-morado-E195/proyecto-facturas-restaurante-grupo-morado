package com.grupo_morado.sistema_facturacion_inventario.infrastructure.websocket;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.MenuEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para la publicación de eventos en tiempo real sobre los menús.
 * Implementa MenuEventPublisherPort usando SimpMessagingTemplate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuEventPublisherAdapter implements MenuEventPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishMenuRefreshEvent() {
        log.info("Publicando evento en tiempo real para refrescar listado de menús.");
        // Envía una simple señal de refresco en lugar del objeto completo
        messagingTemplate.convertAndSend("/topic/menus", "refresh");
    }
}
