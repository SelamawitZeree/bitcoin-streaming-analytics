package cs590.project.order.infrastructure.cart;

import cs590.project.order.domain.cart.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
}

