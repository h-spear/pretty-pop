package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.Order;
import prettypop.shop.repository.custom.OrderRepositoryCustom;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select distinct o from Order o" +
            " left join fetch o.delivery " +
            " left join fetch o.orderItems oi" +
            " left join fetch oi.item" +
            " left join fetch o.member" +
            " where o.id = :orderId")
    Optional<Order> findByIdWithFetchJoin(@Param("orderId") Long orderId);
}
