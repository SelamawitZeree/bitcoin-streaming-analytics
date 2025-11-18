package cs590.project.order.domain.service;

import cs590.project.order.domain.model.Order;
import cs590.project.order.application.event.CartCheckedOutEvent;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(CartCheckedOutEvent event);
    Optional<Order> getOrderById(String orderId);
    List<Order> getOrdersForUser(String userId);
    void processOrderPayment(String orderId);
    void confirmOrder(String orderId);
    void cancelOrder(String orderId);
    void updateOrderStatus(String orderId, Order.OrderStatus newStatus);
}
