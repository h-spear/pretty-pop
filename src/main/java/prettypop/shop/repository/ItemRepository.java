package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prettypop.shop.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
