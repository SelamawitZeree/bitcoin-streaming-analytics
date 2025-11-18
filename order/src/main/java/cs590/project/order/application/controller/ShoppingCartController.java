package cs590.project.order.application.controller;

import cs590.project.order.domain.cart.ShoppingCart;
import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Shopping Cart", description = "Shopping Cart Management APIs")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Operation(summary = "Get a user's shopping cart", description = "Retrieve the entire shopping cart for a given user ID.")
    @GetMapping("/{userId}")
    public ResponseEntity<ShoppingCart> getCart(@PathVariable String userId) {
        ShoppingCart cart = shoppingCartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @Operation(summary = "Add an item to the cart", description = "Add a specified quantity of a product to the user's cart.")
    @PostMapping("/{userId}/items")
    public ResponseEntity<ShoppingCart> addItemToCart(
            @PathVariable String userId,
            @RequestBody CartItemRequest cartItemRequest) {
        ShoppingCart updatedCart = shoppingCartService.addItemToCart(
                userId,
                cartItemRequest.getProductId(),
                cartItemRequest.getQuantity()
        );
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @Operation(summary = "Remove an item from the cart", description = "Remove a specific product from the user's cart.")
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable String productId) {
        shoppingCartService.removeItemFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an item's quantity in the cart", description = "Update the quantity of a specific product in the user's cart.")
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<ShoppingCart> updateItemQuantity(
            @PathVariable String userId,
            @PathVariable String productId,
            @RequestBody QuantityUpdateRequest quantityUpdateRequest) {
        ShoppingCart updatedCart = shoppingCartService.updateItemQuantity(
                userId,
                productId,
                quantityUpdateRequest.getQuantity()
        );
        return ResponseEntity.ok(updatedCart);
    }

    @Operation(summary = "Clear a user's shopping cart", description = "Remove all items from the user's shopping cart.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        shoppingCartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Checkout the shopping cart", description = "Finalize the cart, publish a checkout event, and clear the cart.")
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<String> checkout(
            @PathVariable String userId,
            @RequestBody CheckoutRequest checkoutRequest) {
        shoppingCartService.checkout(
                userId,
                checkoutRequest.getShippingAddress(),
                checkoutRequest.getBillingAddress()
        );
        return ResponseEntity.ok("Checkout successful. Event published.");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CartItemRequest {
        private String productId;
        private int quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class QuantityUpdateRequest {
        private int quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CheckoutRequest {
        private Address shippingAddress;
        private Address billingAddress;
    }
}

