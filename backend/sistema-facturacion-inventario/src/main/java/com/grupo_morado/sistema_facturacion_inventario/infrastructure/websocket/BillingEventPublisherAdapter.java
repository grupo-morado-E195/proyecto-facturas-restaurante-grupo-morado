package com.grupo_morado.sistema_facturacion_inventario.infrastructure.websocket;

import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.BillingEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para la publicación de eventos en tiempo real sobre la facturación.
 * Implementa BillingEventPublisherPort usando SimpMessagingTemplate.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BillingEventPublisherAdapter implements BillingEventPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishBillingRefreshEvent() {
        log.info("Publicando eventos en tiempo real para refrescar listados de órdenes y facturación.");
        messagingTemplate.convertAndSend("/topic/ordenes", "refresh");
        messagingTemplate.convertAndSend("/topic/facturacion", "refresh");
    }
}
