package cs590.project.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> orderItems;
    private double totalPrice;
    private OrderStatus orderStatus;
    private Address shippingAddress;
    private Address billingAddress;
    private LocalDateTime orderDate;

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPED,
        CANCELLED,
        PAYMENT_FAILED,
        STOCK_FAILED
    }

    // Domain methods for order lifecycle management
    public void confirm() {
        if (this.orderStatus == OrderStatus.PENDING) {
            this.orderStatus = OrderStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Order cannot be confirmed from status: " + this.orderStatus);
        }
    }

    public void cancel() {
        if (this.orderStatus == OrderStatus.PENDING || this.orderStatus == OrderStatus.PAYMENT_FAILED || this.orderStatus == OrderStatus.STOCK_FAILED) {
            this.orderStatus = OrderStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Order cannot be cancelled from status: " + this.orderStatus);
        }
    }

    public void markPaymentFailed() {
        if (this.orderStatus == OrderStatus.PENDING) {
            this.orderStatus = OrderStatus.PAYMENT_FAILED;
        } else {
            throw new IllegalStateException("Order payment cannot be marked failed from status: " + this.orderStatus);
        }
    }

    public void markStockFailed() {
        if (this.orderStatus == OrderStatus.PENDING) {
            this.orderStatus = OrderStatus.STOCK_FAILED;
        } else {
            throw new IllegalStateException("Order stock cannot be marked failed from status: " + this.orderStatus);
        }
    }
}
