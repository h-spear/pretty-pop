package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prettypop.shop.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
