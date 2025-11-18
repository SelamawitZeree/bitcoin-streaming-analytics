package cs590.project.order.application.dto;

import cs590.project.order.domain.model.Order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderId;
    private String userId;
    private List<OrderItemDto> orderItems;
    private double totalPrice;
    private OrderStatus orderStatus;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private LocalDateTime orderDate;
}
