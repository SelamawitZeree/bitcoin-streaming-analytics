package cs590.project.order.domain.service;

import cs590.project.order.application.event.CartCheckedOutEvent;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.model.Order.OrderStatus;
import cs590.project.order.infrastructure.repository.OrderEntity;
import cs590.project.order.infrastructure.repository.OrderRepository;
import cs590.project.order.infrastructure.repository.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    // private final KafkaTemplate<String, Object> kafkaTemplate; // For publishing events

    @Override
    @Transactional
    public Order createOrder(CartCheckedOutEvent event) {
        Order order = new Order(
                UUID.randomUUID().toString(),
                event.getUserId(),
                event.getItems(),
                event.getTotalAmount(),
                OrderStatus.PENDING,
                event.getShippingAddress(),
                event.getBillingAddress(),
                LocalDateTime.now()
        );

        OrderEntity orderEntity = orderMapper.toEntity(order);
        orderRepository.save(orderEntity);

        // Publish ReserveStock event
        // kafkaTemplate.send("reserve-stock-topic", new ReserveStockEvent(order.getOrderId(), order.getOrderItems()));

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(orderMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersForUser(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processOrderPayment(String orderId) {
        // This would typically involve publishing an event to a PaymentService
        // For now, we'll just update the status to PENDING (assuming payment processing is external)
        updateOrderStatus(orderId, OrderStatus.PENDING);
        // kafkaTemplate.send("process-payment-topic", new ProcessPaymentEvent(orderId, ...));
    }

    @Override
    @Transactional
    public void confirmOrder(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        Order order = orderMapper.toDomain(orderEntity);
        order.confirm();
        orderRepository.save(orderMapper.toEntity(order));
    }

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        Order order = orderMapper.toDomain(orderEntity);
        order.cancel();
        orderRepository.save(orderMapper.toEntity(order));

        // Publish ReleaseStock event if stock was reserved
        // kafkaTemplate.send("release-stock-topic", new ReleaseStockEvent(order.getOrderId(), order.getOrderItems()));
    }

    @Override
    @Transactional
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        orderEntity.setOrderStatus(newStatus);
        orderRepository.save(orderEntity);
    }
}
