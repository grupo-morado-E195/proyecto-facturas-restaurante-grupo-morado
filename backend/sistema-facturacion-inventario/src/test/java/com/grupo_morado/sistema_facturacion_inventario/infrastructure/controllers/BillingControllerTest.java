package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.in.BillingUseCase;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingControllerTest {

    @Mock
    private BillingUseCase billingUseCase;

    @InjectMocks
    private BillingController billingController;

    @Test
    void invoiceOrder_ShouldReturn200Ok() {
        // Arrange
        OrderResultDTO resultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(),
                OrderStatusEnum.PAGADO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        when(billingUseCase.invoiceOrder(1L)).thenReturn(resultDTO);

        // Act
        ResponseEntity<OrderResultDTO> response = billingController.invoiceOrder(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(OrderStatusEnum.PAGADO, response.getBody().status());

        verify(billingUseCase, times(1)).invoiceOrder(1L);
    }
}
