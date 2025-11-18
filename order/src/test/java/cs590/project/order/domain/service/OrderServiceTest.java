package cs590.project.order.domain.service;

import cs590.project.order.application.event.CartCheckedOutEvent;
import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.model.OrderItem;
import cs590.project.order.infrastructure.repository.OrderEntity;
import cs590.project.order.infrastructure.repository.OrderMapper;
import cs590.project.order.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// Kafka removed - using SQS instead

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    // Kafka removed - using SQS instead
    // private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CartCheckedOutEvent cartCheckedOutEvent;
    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        OrderItem item1 = new OrderItem("prod1", "Product 1", 10.0, 2);
        OrderItem item2 = new OrderItem("prod2", "Product 2", 20.0, 1);
        List<OrderItem> orderItems = Arrays.asList(item1, item2);

        Address shippingAddress = new Address("123 Main St", "Anytown", "CA", "90210", "USA");
        Address billingAddress = new Address("123 Main St", "Anytown", "CA", "90210", "USA");

        cartCheckedOutEvent = new CartCheckedOutEvent(
                "user123",
                orderItems,
                40.0,
                shippingAddress,
                billingAddress,
                LocalDateTime.now()
        );

        order = new Order(
                UUID.randomUUID().toString(),
                "user123",
                orderItems,
                40.0,
                Order.OrderStatus.PENDING,
                shippingAddress,
                billingAddress,
                LocalDateTime.now()
        );

        orderEntity = new OrderEntity();
        orderEntity.setOrderId(order.getOrderId());
        orderEntity.setUserId(order.getUserId());
        orderEntity.setTotalPrice(order.getTotalPrice());
        orderEntity.setOrderStatus(order.getOrderStatus());
        orderEntity.setOrderDate(order.getOrderDate());
        // Assuming orderMapper handles OrderItems and Addresses correctly
    }

    @Test
    void createOrder_shouldCreateAndPublishEvents() {
        when(orderMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDomain(any(OrderEntity.class))).thenReturn(order);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        Order createdOrder = orderService.createOrder(cartCheckedOutEvent);

        assertNotNull(createdOrder);
        assertEquals(order.getOrderId(), createdOrder.getOrderId());
        assertEquals(Order.OrderStatus.PENDING, createdOrder.getOrderStatus());

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        // Kafka removed - using SQS instead
        // verify(kafkaTemplate, times(1)).send(eq("reserve-stock-topic"), any());
        // verify(kafkaTemplate, times(1)).send(eq("process-payment-topic"), any());
    }

    @Test
    void getOrderById_shouldReturnOrderIfExists() {
        when(orderRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(order);

        Optional<Order> foundOrder = orderService.getOrderById(order.getOrderId());

        assertTrue(foundOrder.isPresent());
        assertEquals(order.getOrderId(), foundOrder.get().getOrderId());
    }

    @Test
    void getOrderById_shouldReturnEmptyIfOrderDoesNotExist() {
        when(orderRepository.findByOrderId("nonExistentId")).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderService.getOrderById("nonExistentId");

        assertFalse(foundOrder.isPresent());
    }

    @Test
    void getOrdersForUser_shouldReturnListOfOrders() {
        List<OrderEntity> orderEntities = Arrays.asList(orderEntity);
        when(orderRepository.findByUserId("user123")).thenReturn(orderEntities);
        when(orderMapper.toDomain(orderEntity)).thenReturn(order);

        List<Order> orders = orderService.getOrdersForUser("user123");

        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        assertEquals("user123", orders.get(0).getUserId());
    }

    @Test
    void confirmOrder_shouldUpdateOrderStatusToConfirmed() {
        when(orderRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        orderService.confirmOrder(order.getOrderId());

        assertEquals(Order.OrderStatus.CONFIRMED, order.getOrderStatus());
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    void cancelOrder_shouldUpdateOrderStatusToCancelled() {
        when(orderRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDomain(orderEntity)).thenReturn(order);
        when(orderMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        orderService.cancelOrder(order.getOrderId());

        assertEquals(Order.OrderStatus.CANCELLED, order.getOrderStatus());
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatusCorrectly() {
        when(orderRepository.findByOrderId(order.getOrderId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        orderService.updateOrderStatus(order.getOrderId(), Order.OrderStatus.SHIPPED);

        assertEquals(Order.OrderStatus.SHIPPED, orderEntity.getOrderStatus());
        verify(orderRepository, times(1)).save(orderEntity);
    }
}
