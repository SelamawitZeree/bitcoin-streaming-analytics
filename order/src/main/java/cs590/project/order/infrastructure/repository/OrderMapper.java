package cs590.project.order.infrastructure.repository;

import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(order.getOrderId());
        orderEntity.setUserId(order.getUserId());
        orderEntity.setTotalPrice(order.getTotalPrice());
        orderEntity.setOrderStatus(order.getOrderStatus());
        orderEntity.setOrderDate(order.getOrderDate());

        if (order.getOrderItems() != null) {
            orderEntity.setOrderItems(order.getOrderItems().stream()
                    .map(this::toOrderItemEntity)
                    .collect(Collectors.toList()));
        }

        if (order.getShippingAddress() != null) {
            orderEntity.setShippingAddress(toAddressEmbeddable(order.getShippingAddress()));
        }
        if (order.getBillingAddress() != null) {
            orderEntity.setBillingAddress(toAddressEmbeddable(order.getBillingAddress()));
        }

        return orderEntity;
    }

    public Order toDomain(OrderEntity orderEntity) {
        if (orderEntity == null) {
            return null;
        }
        Order order = new Order();
        order.setOrderId(orderEntity.getOrderId());
        order.setUserId(orderEntity.getUserId());
        order.setTotalPrice(orderEntity.getTotalPrice());
        order.setOrderStatus(orderEntity.getOrderStatus());
        order.setOrderDate(orderEntity.getOrderDate());

        if (orderEntity.getOrderItems() != null) {
            order.setOrderItems(orderEntity.getOrderItems().stream()
                    .map(this::toOrderItemDomain)
                    .collect(Collectors.toList()));
        }

        if (orderEntity.getShippingAddress() != null) {
            order.setShippingAddress(toAddressDomain(orderEntity.getShippingAddress()));
        }
        if (orderEntity.getBillingAddress() != null) {
            order.setBillingAddress(toAddressDomain(orderEntity.getBillingAddress()));
        }

        return order;
    }

    private OrderItemEntity toOrderItemEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setProductId(orderItem.getProductId());
        orderItemEntity.setProductName(orderItem.getProductName());
        orderItemEntity.setPrice(orderItem.getPrice());
        orderItemEntity.setQuantity(orderItem.getQuantity());
        return orderItemEntity;
    }

    private OrderItem toOrderItemDomain(OrderItemEntity orderItemEntity) {
        if (orderItemEntity == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(orderItemEntity.getProductId());
        orderItem.setProductName(orderItemEntity.getProductName());
        orderItem.setPrice(orderItemEntity.getPrice());
        orderItem.setQuantity(orderItemEntity.getQuantity());
        return orderItem;
    }

    private AddressEmbeddable toAddressEmbeddable(Address address) {
        if (address == null) {
            return null;
        }
        AddressEmbeddable addressEmbeddable = new AddressEmbeddable();
        addressEmbeddable.setStreet(address.getStreet());
        addressEmbeddable.setCity(address.getCity());
        addressEmbeddable.setState(address.getState());
        addressEmbeddable.setZipCode(address.getZipCode());
        addressEmbeddable.setCountry(address.getCountry());
        return addressEmbeddable;
    }

    private Address toAddressDomain(AddressEmbeddable addressEmbeddable) {
        if (addressEmbeddable == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(addressEmbeddable.getStreet());
        address.setCity(addressEmbeddable.getCity());
        address.setState(addressEmbeddable.getState());
        address.setZipCode(addressEmbeddable.getZipCode());
        address.setCountry(addressEmbeddable.getCountry());
        return address;
    }
}
