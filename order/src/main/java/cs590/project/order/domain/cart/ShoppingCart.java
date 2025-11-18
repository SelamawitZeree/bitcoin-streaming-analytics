package cs590.project.order.domain.cart;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@RedisHash("ShoppingCart")
public class ShoppingCart {
    @Id
    private String userId;
    private List<CartItem> items;

    public ShoppingCart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public Optional<CartItem> getItemByProductId(String productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
    }

    public void addItem(CartItem newItem) {
        getItemByProductId(newItem.getProductId()).ifPresentOrElse(
                existingItem -> existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity()),
                () -> items.add(newItem)
        );
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void updateItemQuantity(String productId, int quantity) {
        getItemByProductId(productId).ifPresent(item -> item.setQuantity(quantity));
    }

    public void clear() {
        items.clear();
    }
}

