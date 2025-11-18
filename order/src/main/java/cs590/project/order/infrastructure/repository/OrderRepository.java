package cs590.project.order.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    Optional<OrderEntity> findByOrderId(String orderId);
    List<OrderEntity> findByUserId(String userId);
}
