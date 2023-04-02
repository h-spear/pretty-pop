package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi" +
            " join oi.order o" +
            " where o.member.id = :memberId and" +
            "       oi.item.id = :itemId and" +
            "       oi.hasReview = false")
    List<OrderItem> getNotReviewedItem(@Param("memberId") Long memberId, @Param("itemId") Long itemId);

    @Modifying
    @Query("update OrderItem oi set oi.hasReview = true where oi in :orderItems")
    int checkHasReview(@Param("orderItems") List<OrderItem> orderItems);
}
