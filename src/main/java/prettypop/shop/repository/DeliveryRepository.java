package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prettypop.shop.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
