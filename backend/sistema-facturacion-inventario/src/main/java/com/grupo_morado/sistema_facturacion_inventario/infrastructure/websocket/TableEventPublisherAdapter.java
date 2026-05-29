package com.grupo_morado.sistema_facturacion_inventario.infrastructure.websocket;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.TableResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida para la publicación de eventos en tiempo real sobre las mesas.
 * Implementa TableEventPublisherPort usando SimpMessagingTemplate de Spring.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TableEventPublisherAdapter implements TableEventPublisherPort {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void publishTableEvent(TableResultDTO table) {
        log.info("Publicando evento en tiempo real para la mesa número: {}", table.number());
        messagingTemplate.convertAndSend("/topic/mesas", table);
    }
}
