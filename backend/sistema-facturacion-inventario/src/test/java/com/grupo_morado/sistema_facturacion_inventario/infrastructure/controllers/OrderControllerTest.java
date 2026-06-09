package com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderDetailResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.services.OrderService;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.controllers.dtos.OrderStatusUpdateDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void cancelOrder_ShouldReturn200Ok() {
        // Act
        ResponseEntity<Map<String, String>> response = orderController.cancelOrder(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Orden cancelada correctamente.", response.getBody().get("message"));

        verify(orderService, times(1)).cancelOrder(1L);
    }

    @Test
    void getOrderById_ShouldReturn200Ok() {
        // Arrange
        OrderResultDTO resultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(new OrderDetailResultDTO(10L, "Pizza", 2, "Sin cebolla", BigDecimal.valueOf(50.00), BigDecimal.valueOf(100.00))),
                OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(8.00),
                BigDecimal.valueOf(108.00)
        );

        when(orderService.getOrderById(1L)).thenReturn(resultDTO);

        // Act
        ResponseEntity<OrderResultDTO> response = orderController.getOrderById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(5, response.getBody().tableNumber());
        assertEquals(1, response.getBody().details().size());
        assertEquals("Pizza", response.getBody().details().getFirst().nombrePlato());

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrders_ShouldReturn200Ok() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        OrderSummaryResultDTO orderSummaryDTO = new OrderSummaryResultDTO(
                1L,
                5,
                OrderStatusEnum.PENDIENTE,
                "Juan Mesero",
                null
        );
        Page<OrderSummaryResultDTO> pageResult = new PageImpl<>(List.of(orderSummaryDTO));

        when(orderService.getOrders(OrderStatusEnum.PENDIENTE, pageable)).thenReturn(pageResult);

        // Act
        ResponseEntity<Page<OrderSummaryResultDTO>> response = orderController.getOrders(OrderStatusEnum.PENDIENTE, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(1L, response.getBody().getContent().getFirst().id());
        assertEquals(5, response.getBody().getContent().getFirst().numeroMesa());
        assertEquals(OrderStatusEnum.PENDIENTE, response.getBody().getContent().getFirst().estado());
        assertEquals("Juan Mesero", response.getBody().getContent().getFirst().nombreMesero());

        verify(orderService, times(1)).getOrders(OrderStatusEnum.PENDIENTE, pageable);
    }

    @Test
    void updateOrderStatus_ShouldReturn200Ok() {
        // Arrange
        OrderStatusUpdateDTO updateDTO = new OrderStatusUpdateDTO(OrderStatusEnum.EN_PREPARACION);
        OrderResultDTO resultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(),
                OrderStatusEnum.EN_PREPARACION,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        when(orderService.updateOrderStatus(1L, OrderStatusEnum.EN_PREPARACION)).thenReturn(resultDTO);

        // Act
        ResponseEntity<OrderResultDTO> response = orderController.updateOrderStatus(1L, updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(OrderStatusEnum.EN_PREPARACION, response.getBody().status());

        verify(orderService, times(1)).updateOrderStatus(1L, OrderStatusEnum.EN_PREPARACION);
    }

    @Test
    void createOrder_ShouldReturn201Created() {
        // Arrange
        com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO createDTO = 
                new com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO(1L, List.of());
        org.springframework.security.core.userdetails.UserDetails userDetails = mock(org.springframework.security.core.userdetails.UserDetails.class);
        when(userDetails.getUsername()).thenReturn("mesero@restaurant.com");

        OrderResultDTO resultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(),
                OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(8.00),
                BigDecimal.valueOf(108.00)
        );

        when(orderService.createOrder(createDTO, "mesero@restaurant.com")).thenReturn(resultDTO);

        // Act
        ResponseEntity<OrderResultDTO> response = orderController.createOrder(createDTO, userDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(OrderStatusEnum.PENDIENTE, response.getBody().status());

        verify(orderService, times(1)).createOrder(createDTO, "mesero@restaurant.com");
    }

    @Test
    void updateOrder_ShouldReturn200Ok() {
        // Arrange
        OrderUpdateDTO updateDTO = new OrderUpdateDTO(2L, List.of());
        OrderResultDTO resultDTO = new OrderResultDTO(
                1L,
                6,
                List.of(),
                OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(120.00),
                BigDecimal.valueOf(9.60),
                BigDecimal.valueOf(129.60)
        );

        when(orderService.updateOrder(1L, updateDTO)).thenReturn(resultDTO);

        // Act
        ResponseEntity<OrderResultDTO> response = orderController.updateOrder(1L, updateDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(6, response.getBody().tableNumber());
        assertEquals(OrderStatusEnum.PENDIENTE, response.getBody().status());

        verify(orderService, times(1)).updateOrder(1L, updateDTO);
    }
}
