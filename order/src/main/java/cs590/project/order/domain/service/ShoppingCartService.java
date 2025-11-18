package cs590.project.order.domain.service;

import cs590.project.order.application.dto.ProductDto;
import cs590.project.order.application.event.CartCheckedOutEvent;
import cs590.project.order.domain.cart.CartItem;
import cs590.project.order.domain.cart.ShoppingCart;
import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.OrderItem;
import cs590.project.order.infrastructure.cart.ShoppingCartRepository;
import cs590.project.order.infrastructure.sqs.SQSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final RestTemplate restTemplate;
    private final SQSService sqsService;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, RestTemplate restTemplate, SQSService sqsService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.restTemplate = restTemplate;
        this.sqsService = sqsService;
    }

    public ShoppingCart getCart(String userId) {
        return shoppingCartRepository.findById(userId).orElse(new ShoppingCart(userId));
    }

    public ShoppingCart addItemToCart(String userId, String productId, int quantity) {
        ShoppingCart cart = getCart(userId);
        ProductDto product = fetchProductDetails(productId);

        if (product != null) {
            CartItem newItem = new CartItem(product.getProductId(), product.getName(), product.getPrice(), quantity);
            cart.addItem(newItem);
            return shoppingCartRepository.save(cart);
        }
        throw new RuntimeException("Product not found: " + productId);
    }

    public ShoppingCart removeItemFromCart(String userId, String productId) {
        ShoppingCart cart = getCart(userId);
        cart.removeItem(productId);
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart updateItemQuantity(String userId, String productId, int quantity) {
        ShoppingCart cart = getCart(userId);
        cart.updateItemQuantity(productId, quantity);
        return shoppingCartRepository.save(cart);
    }

    public void clearCart(String userId) {
        shoppingCartRepository.deleteById(userId);
    }

    public void checkout(String userId, Address shippingAddress, Address billingAddress) {
        ShoppingCart cart = getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart.");
        }

        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // Convert CartItem to OrderItem
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toList());

        CartCheckedOutEvent event = new CartCheckedOutEvent(
                userId,
                orderItems,
                totalAmount,
                shippingAddress,
                billingAddress,
                LocalDateTime.now()
        );
        sqsService.sendMessage(event);

        clearCart(userId); // Clear the cart after checkout
    }

    private ProductDto fetchProductDetails(String productId) {
        String url = productServiceUrl + "/products/" + productId;
        return restTemplate.getForObject(url, ProductDto.class);
    }
}

