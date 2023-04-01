package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prettypop.shop.entity.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o" +
            " left join fetch o.orderItems oi" +
            " left join fetch oi.item " +
            " left join fetch o.member")
    Optional<Order> findByIdWithFetchJoin(Long orderId);
}
