package cs590.project.order.application.dto;

import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDtoMapper {

    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getOrderId());
        orderDto.setUserId(order.getUserId());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setOrderStatus(order.getOrderStatus());
        orderDto.setOrderDate(order.getOrderDate());

        if (order.getOrderItems() != null) {
            orderDto.setOrderItems(order.getOrderItems().stream()
                    .map(this::toOrderItemDto)
                    .collect(Collectors.toList()));
        }

        if (order.getShippingAddress() != null) {
            orderDto.setShippingAddress(toAddressDto(order.getShippingAddress()));
        }
        if (order.getBillingAddress() != null) {
            orderDto.setBillingAddress(toAddressDto(order.getBillingAddress()));
        }

        return orderDto;
    }

    public List<OrderDto> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private OrderItemDto toOrderItemDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProductId(orderItem.getProductId());
        orderItemDto.setProductName(orderItem.getProductName());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setQuantity(orderItem.getQuantity());
        return orderItemDto;
    }

    private AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setZipCode(address.getZipCode());
        addressDto.setCountry(address.getCountry());
        return addressDto;
    }
}
