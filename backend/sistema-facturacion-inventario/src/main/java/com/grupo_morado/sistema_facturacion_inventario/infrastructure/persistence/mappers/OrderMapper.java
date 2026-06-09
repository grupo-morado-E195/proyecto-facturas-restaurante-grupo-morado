package com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderDetailResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Mapper para transformar la entidad Order en su DTO de resultado OrderResultDTO.
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderProviderPort orderProviderPort;

    /**
     * Convierte una entidad Order a su DTO de resultado OrderResultDTO,
     * obteniendo automáticamente los detalles de la orden a través del puerto.
     *
     * @param order Entidad orden a convertir.
     * @return DTO de resultado.
     */
    public OrderResultDTO entityToResult(Order order) {
        if (order == null) {
            return null;
        }

        // Buscar los detalles de la orden
        List<OrderDetail> details = orderProviderPort.findDetailsByOrderId(order.getId());
        List<OrderDetailResultDTO> detailDTOs = details == null ? Collections.emptyList() : details.stream()
                .map(this::toDetailDTO)
                .toList();

        Integer tableNumber = order.getTable() != null ? order.getTable().getNumber() : null;

        return new OrderResultDTO(
                order.getId(),
                tableNumber,
                detailDTOs,
                order.getStatus(),
                order.getSubtotal(),
                order.getConsumptionTax(),
                order.getTotal()
        );
    }

    private OrderDetailResultDTO toDetailDTO(OrderDetail detail) {
        if (detail == null) {
            return null;
        }

        String nombrePlato = detail.getDish() != null ? detail.getDish().getName() : null;
        BigDecimal precioUnitario = detail.getPrice();
        BigDecimal subtotalDetalle = BigDecimal.ZERO;
        
        if (precioUnitario != null && detail.getQuantity() != null) {
            subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detail.getQuantity()));
        }

        return new OrderDetailResultDTO(
                detail.getDish() != null ? detail.getDish().getId() : null,
                nombrePlato,
                detail.getQuantity(),
                detail.getObservation(),
                precioUnitario,
                subtotalDetalle
        );
    }

    /**
     * Convierte una entidad Order a su DTO de resumen OrderSummaryResultDTO.
     *
     * @param order Entidad orden a convertir.
     * @return DTO de resumen.
     */
    public OrderSummaryResultDTO entityToSummary(Order order) {
        if (order == null) {
            return null;
        }

        Integer numeroMesa = order.getTable() != null ? order.getTable().getNumber() : null;
        String nombreMesero = null;
        if (order.getWaiter() != null) {
            nombreMesero = order.getWaiter().getName() + " " + order.getWaiter().getLastname();
        }

        return new OrderSummaryResultDTO(
                order.getId(),
                numeroMesa,
                order.getStatus(),
                nombreMesero,
                order.getCreatedAt()
        );
    }
}
