package com.grupo_morado.sistema_facturacion_inventario.application.services;

import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderDetailCreateDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderDetailResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderSummaryResultDTO;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.TableProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.UserProviderPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.OrderEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.application.ports.out.DishEventPublisherPort;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.DisponibilityStateEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.OrderStatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.domain.enums.StatusEnum;
import com.grupo_morado.sistema_facturacion_inventario.application.dtos.OrderUpdateDTO;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.EmptyOrderException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InsufficientStockException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.NotFoundException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.InvalidOrderStatusTransitionException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.TableNotAvailableException;
import com.grupo_morado.sistema_facturacion_inventario.domain.exceptions.OrderCannotBeModifiedException;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Dish;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Order;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.OrderDetail;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.Table;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.User;
import com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.mappers.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderProviderPort orderProviderPort;

    @Mock
    private DishProviderPort dishProviderPort;

    @Mock
    private TableProviderPort tableProviderPort;

    @Mock
    private OrderEventPublisherPort orderEventPublisherPort;

    @Mock
    private DishEventPublisherPort dishEventPublisherPort;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserProviderPort userProviderPort;

    @InjectMocks
    private OrderService orderService;

    private Order orderEntity;
    private OrderResultDTO orderResultDTO;

    @BeforeEach
    void setUp() {
        Table table = new Table();
        table.setNumber(5);

        orderEntity = new Order();
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(orderEntity, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        orderEntity.setTable(table);
        orderEntity.setStatus(OrderStatusEnum.PENDIENTE);
        orderEntity.setSubtotal(BigDecimal.valueOf(100.00));
        orderEntity.setConsumptionTax(BigDecimal.valueOf(8.00));
        orderEntity.setTotal(BigDecimal.valueOf(108.00));

        orderResultDTO = new OrderResultDTO(
                1L,
                5,
                List.of(new OrderDetailResultDTO("Pizza", 2, "Sin cebolla", BigDecimal.valueOf(50.00), BigDecimal.valueOf(100.00))),
                OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(8.00),
                BigDecimal.valueOf(108.00)
        );

        try {
            java.lang.reflect.Field taxField = OrderService.class.getDeclaredField("taxPercentage");
            taxField.setAccessible(true);
            taxField.set(orderService, BigDecimal.valueOf(0.08));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getOrderById_WhenOrderExists_ShouldReturnOrderResultDTO() {
        // Arrange
        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.entityToResult(orderEntity)).thenReturn(orderResultDTO);

        // Act
        OrderResultDTO result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(5, result.tableNumber());
        assertEquals(1, result.details().size());
        assertEquals("Pizza", result.details().getFirst().nombrePlato());
        assertEquals(OrderStatusEnum.PENDIENTE, result.status());
        assertEquals(BigDecimal.valueOf(100.00), result.subtotal());
        assertEquals(BigDecimal.valueOf(8.00), result.consumptionTax());
        assertEquals(BigDecimal.valueOf(108.00), result.total());

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderMapper, times(1)).entityToResult(orderEntity);
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        when(orderProviderPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.getOrderById(1L));

        assertEquals("La orden con id '1' no fue encontrada.", exception.getMessage());

        verify(orderProviderPort, times(1)).findById(1L);
        verifyNoInteractions(orderMapper);
    }

    @Test
    void getOrders_WhenNoStatusFilter_ShouldReturnAllOrdersSortedByCreatedAtAsc() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(orderEntity));
        OrderSummaryResultDTO orderSummaryDTO = new OrderSummaryResultDTO(
                1L,
                5,
                OrderStatusEnum.PENDIENTE,
                "Juan Mesero",
                null
        );

        when(orderProviderPort.findByStatus(null, pageable)).thenReturn(orderPage);
        when(orderMapper.entityToSummary(orderEntity)).thenReturn(orderSummaryDTO);

        // Act
        Page<OrderSummaryResultDTO> result = orderService.getOrders(null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());
        assertEquals(5, result.getContent().getFirst().numeroMesa());
        assertEquals(OrderStatusEnum.PENDIENTE, result.getContent().getFirst().estado());
        assertEquals("Juan Mesero", result.getContent().getFirst().nombreMesero());

        verify(orderProviderPort, times(1)).findByStatus(null, pageable);
        verify(orderMapper, times(1)).entityToSummary(orderEntity);
    }

    @Test
    void getOrders_WithStatusFilter_ShouldReturnFilteredOrdersSortedByCreatedAtAsc() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(orderEntity));
        OrderSummaryResultDTO orderSummaryDTO = new OrderSummaryResultDTO(
                1L,
                5,
                OrderStatusEnum.PENDIENTE,
                "Juan Mesero",
                null
        );

        when(orderProviderPort.findByStatus(OrderStatusEnum.PENDIENTE, pageable)).thenReturn(orderPage);
        when(orderMapper.entityToSummary(orderEntity)).thenReturn(orderSummaryDTO);

        // Act
        Page<OrderSummaryResultDTO> result = orderService.getOrders(OrderStatusEnum.PENDIENTE, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());

        verify(orderProviderPort, times(1)).findByStatus(OrderStatusEnum.PENDIENTE, pageable);
        verify(orderMapper, times(1)).entityToSummary(orderEntity);
    }

    @Test
    void updateOrderStatus_WhenPendienteToEnPreparacion_ShouldUpdateAndReturnResult() {
        // Arrange
        orderEntity.setStatus(OrderStatusEnum.PENDIENTE);
        OrderResultDTO updatedResult = new OrderResultDTO(
                1L, 5, List.of(), OrderStatusEnum.EN_PREPARACION,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderProviderPort.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapper.entityToResult(orderEntity)).thenReturn(updatedResult);

        // Act
        OrderResultDTO result = orderService.updateOrderStatus(1L, OrderStatusEnum.EN_PREPARACION);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatusEnum.EN_PREPARACION, result.status());

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, times(1)).save(orderEntity);
        verify(orderMapper, times(1)).entityToResult(orderEntity);
    }

    @Test
    void updateOrderStatus_WhenEnPreparacionToListo_ShouldUpdateAndReturnResult() {
        // Arrange
        orderEntity.setStatus(OrderStatusEnum.EN_PREPARACION);
        OrderResultDTO updatedResult = new OrderResultDTO(
                1L, 5, List.of(), OrderStatusEnum.LISTO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );

        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderProviderPort.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapper.entityToResult(orderEntity)).thenReturn(updatedResult);

        // Act
        OrderResultDTO result = orderService.updateOrderStatus(1L, OrderStatusEnum.LISTO);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatusEnum.LISTO, result.status());

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, times(1)).save(orderEntity);
        verify(orderMapper, times(1)).entityToResult(orderEntity);
    }

    @Test
    void updateOrderStatus_WhenInvalidTransition_ShouldThrowInvalidOrderStatusTransitionException() {
        // Arrange
        orderEntity.setStatus(OrderStatusEnum.PENDIENTE);

        when(orderProviderPort.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act & Assert
        assertThrows(InvalidOrderStatusTransitionException.class, () -> orderService.updateOrderStatus(1L, OrderStatusEnum.LISTO));

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, never()).save(any());
        verifyNoInteractions(orderMapper);
    }

    @Test
    void updateOrderStatus_WhenOrderNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(orderProviderPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> orderService.updateOrderStatus(1L, OrderStatusEnum.EN_PREPARACION));

        verify(orderProviderPort, times(1)).findById(1L);
        verify(orderProviderPort, never()).save(any());
        verifyNoInteractions(orderMapper);
    }

    @Test
    void createOrder_WhenValid_ShouldCreateOrderAndDecrementStock() {
        // Arrange
        OrderCreateDTO dto = new OrderCreateDTO(1L, List.of(new OrderDetailCreateDTO(10L, 2, "Sin picante")));
        String email = "mesero@restaurant.com";

        Table table = new Table();
        table.setNumber(3);
        table.setStatus(StatusEnum.ACTIVO);
        table.setDisponibility(DisponibilityStateEnum.DISPONIBLE);
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(table, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        User waiter = new User();
        waiter.setEmail(email);

        Dish dish = new Dish();
        dish.setName("Tacos");
        dish.setPrice(BigDecimal.valueOf(15.00));
        dish.setStock(10);
        dish.setStatus(StatusEnum.ACTIVO);
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dish, 10L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        when(tableProviderPort.findById(1L)).thenReturn(Optional.of(table));
        when(userProviderPort.findByEmail(email)).thenReturn(Optional.of(waiter));
        when(dishProviderPort.findById(10L)).thenReturn(Optional.of(dish));

        when(orderProviderPort.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(o, 100L);
            } catch (Exception e) {
                fail(e.getMessage());
            }
            return o;
        });

        OrderResultDTO expectedResult = new OrderResultDTO(
                100L, 3, List.of(), OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(30.00), BigDecimal.valueOf(2.40), BigDecimal.valueOf(32.40)
        );
        when(orderMapper.entityToResult(any(Order.class))).thenReturn(expectedResult);

        // Act
        OrderResultDTO result = orderService.createOrder(dto, email);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(3, result.tableNumber());
        assertEquals(BigDecimal.valueOf(32.40), result.total());

        // Verificaciones
        assertEquals(DisponibilityStateEnum.OCUPADA, table.getDisponibility());
        assertEquals(8, dish.getStock());

        verify(tableProviderPort, times(1)).findById(1L);
        verify(tableProviderPort, times(1)).save(table);
        verify(userProviderPort, times(1)).findByEmail(email);
        verify(dishProviderPort, times(1)).findById(10L);
        verify(dishProviderPort, times(1)).save(dish);
        verify(orderProviderPort, times(1)).save(any(Order.class));
        verify(orderProviderPort, times(1)).saveDetails(anyList());
        verify(orderEventPublisherPort, times(1)).publishOrderRefreshEvent();
        verify(dishEventPublisherPort, times(1)).publishDishRefreshEvent();
    }

    @Test
    void createOrder_WhenEmptyDetails_ShouldThrowEmptyOrderException() {
        // Arrange
        OrderCreateDTO dto = new OrderCreateDTO(1L, List.of());
        String email = "mesero@restaurant.com";

        // Act & Assert
        assertThrows(EmptyOrderException.class, () -> orderService.createOrder(dto, email));

        verifyNoInteractions(tableProviderPort);
        verifyNoInteractions(userProviderPort);
        verifyNoInteractions(dishProviderPort);
        verifyNoInteractions(orderProviderPort);
    }

    @Test
    void createOrder_WhenTableNotAvailable_ShouldThrowTableNotAvailableException() {
        // Arrange
        OrderCreateDTO dto = new OrderCreateDTO(1L, List.of(new OrderDetailCreateDTO(10L, 2, "")));
        String email = "mesero@restaurant.com";

        Table table = new Table();
        table.setNumber(3);
        table.setStatus(StatusEnum.ACTIVO);
        table.setDisponibility(DisponibilityStateEnum.OCUPADA); // ya ocupada!

        when(tableProviderPort.findById(1L)).thenReturn(Optional.of(table));

        // Act & Assert
        assertThrows(TableNotAvailableException.class, () -> orderService.createOrder(dto, email));

        verify(tableProviderPort, times(1)).findById(1L);
        verify(tableProviderPort, never()).save(any());
        verifyNoInteractions(userProviderPort);
    }

    @Test
    void createOrder_WhenInsufficientStock_ShouldThrowInsufficientStockException() {
        // Arrange
        OrderCreateDTO dto = new OrderCreateDTO(1L, List.of(new OrderDetailCreateDTO(10L, 5, "")));
        String email = "mesero@restaurant.com";

        Table table = new Table();
        table.setNumber(3);
        table.setStatus(StatusEnum.ACTIVO);
        table.setDisponibility(DisponibilityStateEnum.DISPONIBLE);

        User waiter = new User();
        waiter.setEmail(email);

        Dish dish = new Dish();
        dish.setName("Tacos");
        dish.setStock(2); // solo 2 en stock
        dish.setStatus(StatusEnum.ACTIVO);

        when(tableProviderPort.findById(1L)).thenReturn(Optional.of(table));
        when(userProviderPort.findByEmail(email)).thenReturn(Optional.of(waiter));
        when(dishProviderPort.findById(10L)).thenReturn(Optional.of(dish));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(dto, email));

        verify(tableProviderPort, times(1)).findById(1L);
        verify(userProviderPort, times(1)).findByEmail(email);
        verify(dishProviderPort, times(1)).findById(10L);
        verify(dishProviderPort, never()).save(any());
        verify(orderProviderPort, never()).save(any());
    }

    @Test
    void updateOrder_WhenValid_ShouldModifyOrderRestoreOldStockAndDiscountNewStock() {
        // Arrange
        OrderUpdateDTO dto = new OrderUpdateDTO(2L, List.of(new OrderDetailCreateDTO(10L, 3, "Extra queso")));

        Table oldTable = new Table();
        oldTable.setNumber(5);
        oldTable.setStatus(StatusEnum.ACTIVO);
        oldTable.setDisponibility(DisponibilityStateEnum.OCUPADA);
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(oldTable, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Table newTable = new Table();
        newTable.setNumber(6);
        newTable.setStatus(StatusEnum.ACTIVO);
        newTable.setDisponibility(DisponibilityStateEnum.DISPONIBLE);
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(newTable, 2L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDIENTE);
        order.setTable(oldTable);
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, 100L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Dish oldDish = new Dish();
        oldDish.setName("Tacos");
        oldDish.setStock(5);
        oldDish.setStatus(StatusEnum.ACTIVO);

        OrderDetail oldDetail = new OrderDetail();
        oldDetail.setDish(oldDish);
        oldDetail.setQuantity(2);
        oldDetail.setPrice(BigDecimal.valueOf(10.00));

        Dish newDish = new Dish();
        newDish.setName("Burrito");
        newDish.setStock(10);
        newDish.setStatus(StatusEnum.ACTIVO);
        newDish.setPrice(BigDecimal.valueOf(12.00));
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(newDish, 10L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        when(orderProviderPort.findById(100L)).thenReturn(Optional.of(order));
        when(tableProviderPort.findById(2L)).thenReturn(Optional.of(newTable));
        when(orderProviderPort.findDetailsByOrderId(100L)).thenReturn(List.of(oldDetail));
        when(dishProviderPort.findById(10L)).thenReturn(Optional.of(newDish));

        OrderResultDTO expectedResult = new OrderResultDTO(
                100L, 6, List.of(), OrderStatusEnum.PENDIENTE,
                BigDecimal.valueOf(36.00), BigDecimal.valueOf(2.88), BigDecimal.valueOf(38.88)
        );
        when(orderMapper.entityToResult(order)).thenReturn(expectedResult);

        // Act
        OrderResultDTO result = orderService.updateOrder(100L, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(6, result.tableNumber());
        assertEquals(BigDecimal.valueOf(38.88), result.total());

        // Verificaciones
        assertEquals(DisponibilityStateEnum.DISPONIBLE, oldTable.getDisponibility());
        assertEquals(DisponibilityStateEnum.OCUPADA, newTable.getDisponibility());
        assertEquals(7, oldDish.getStock()); // 5 + 2 devueltos
        assertEquals(7, newDish.getStock()); // 10 - 3 pedidos

        verify(orderProviderPort, times(1)).findById(100L);
        verify(tableProviderPort, times(1)).findById(2L);
        verify(tableProviderPort, times(1)).save(oldTable);
        verify(tableProviderPort, times(1)).save(newTable);
        verify(orderProviderPort, times(1)).findDetailsByOrderId(100L);
        verify(dishProviderPort, times(1)).save(oldDish);
        verify(dishProviderPort, times(1)).save(newDish);
        verify(orderProviderPort, times(1)).deleteDetails(anyList());
        verify(orderProviderPort, times(1)).save(order);
        verify(orderProviderPort, times(1)).saveDetails(anyList());
        verify(orderEventPublisherPort, times(1)).publishOrderRefreshEvent();
        verify(dishEventPublisherPort, times(1)).publishDishRefreshEvent();
    }

    @Test
    void updateOrder_WhenEmptyDetails_ShouldThrowEmptyOrderException() {
        OrderUpdateDTO dto = new OrderUpdateDTO(1L, List.of());
        assertThrows(EmptyOrderException.class, () -> orderService.updateOrder(100L, dto));
        verifyNoInteractions(orderProviderPort);
    }

    @Test
    void updateOrder_WhenOrderNotFound_ShouldThrowNotFoundException() {
        OrderUpdateDTO dto = new OrderUpdateDTO(1L, List.of(new OrderDetailCreateDTO(10L, 2, "")));
        when(orderProviderPort.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(100L, dto));
        verify(orderProviderPort, times(1)).findById(100L);
        verifyNoMoreInteractions(orderProviderPort);
    }

    @Test
    void updateOrder_WhenOrderNotPendiente_ShouldThrowOrderCannotBeModifiedException() {
        OrderUpdateDTO dto = new OrderUpdateDTO(1L, List.of(new OrderDetailCreateDTO(10L, 2, "")));
        Order order = new Order();
        order.setStatus(OrderStatusEnum.EN_PREPARACION);

        when(orderProviderPort.findById(100L)).thenReturn(Optional.of(order));

        assertThrows(OrderCannotBeModifiedException.class, () -> orderService.updateOrder(100L, dto));

        verify(orderProviderPort, times(1)).findById(100L);
        verifyNoMoreInteractions(orderProviderPort);
    }

    @Test
    void updateOrder_WhenNewTableNotAvailable_ShouldThrowTableNotAvailableException() {
        OrderUpdateDTO dto = new OrderUpdateDTO(2L, List.of(new OrderDetailCreateDTO(10L, 2, "")));

        Table oldTable = new Table();
        try {
            java.lang.reflect.Field idField = com.grupo_morado.sistema_facturacion_inventario.infrastructure.persistence.entities.BaseEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(oldTable, 1L);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        Order order = new Order();
        order.setStatus(OrderStatusEnum.PENDIENTE);
        order.setTable(oldTable);

        Table newTable = new Table();
        newTable.setStatus(StatusEnum.ACTIVO);
        newTable.setDisponibility(DisponibilityStateEnum.OCUPADA); // ya ocupada!

        when(orderProviderPort.findById(100L)).thenReturn(Optional.of(order));
        when(tableProviderPort.findById(2L)).thenReturn(Optional.of(newTable));

        assertThrows(TableNotAvailableException.class, () -> orderService.updateOrder(100L, dto));

        verify(orderProviderPort, times(1)).findById(100L);
        verify(tableProviderPort, times(1)).findById(2L);
        verify(tableProviderPort, never()).save(any());
    }
}
