package cs590.project.order.application.event;

import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCheckedOutEvent {
    private String userId;
    private List<OrderItem> items;
    private double totalAmount;
    private Address shippingAddress;
    private Address billingAddress;
    private LocalDateTime checkoutTime;
}
