package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.BillingEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeInvoicedException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private OrderProviderPort orderProviderPort;

    @Mock
    private TableProviderPort tableProviderPort;

    @Mock
    private BillingEventPublisherPort billingEventPublisherPort;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private BillingService billingService;

    private Order orderEntity;
    private Table tableEntity;
    private OrderResultDTO orderResultDTO;

    @BeforeEach
    void setUp() {
        tableEntity = new Table();
        tableEntity.setNumber(5);
        tableEntity.setDisponibility(DisponibilityStateEnum.OCUPADA);

        orderEntity = new Order();
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(orderEntity, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        orderEntity.setTable(tableEntity);
        orderEntity.setStatus(OrderStatusEnum.LISTO);

        orderResultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(),
                OrderStatusEnum.PAGADO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    @Test
    void invoiceOrder_WhenListo_ShouldInvoiceAndFreeTableAndReturnResult() {
        // Arrange
        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderProviderPort.save(orderEntity)).thenReturn(orderEntity);
        when(tableProviderPort.save(tableEntity)).thenReturn(tableEntity);
        when(orderMapper.entityToResult(orderEntity)).thenReturn(orderResultDTO);

        // Act
        OrderResultDTO result = billingService.invoiceOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatusEnum.PAGADO, result.status());
        assertEquals(DisponibilityStateEnum.DISPONIBLE, tableEntity.getDisponibility());

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, times(1)).save(orderEntity);
        verify(tableProviderPort, times(1)).save(tableEntity);
        verify(billingEventPublisherPort, times(1)).publishBillingRefreshEvent();
        verify(orderMapper, times(1)).entityToResult(orderEntity);
    }

    @Test
    void invoiceOrder_WhenNotListo_ShouldThrowOrderCannotBeInvoicedException() {
        // Arrange
        orderEntity.setStatus(OrderStatusEnum.PENDIENTE);
        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act & Assert
        assertThrows(OrderCannotBeInvoicedException.class, () -> billingService.invoiceOrder(1L));

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, never()).save(any());
        verify(tableProviderPort, never()).save(any());
        verifyNoInteractions(billingEventPublisherPort);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void invoiceOrder_WhenNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(orderProviderPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> billingService.invoiceOrder(1L));

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, never()).save(any());
        verify(tableProviderPort, never()).save(any());
        verifyNoInteractions(billingEventPublisherPort);
        verifyNoInteractions(orderMapper);
    }
}
