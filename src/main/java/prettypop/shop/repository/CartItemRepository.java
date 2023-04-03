package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci where ci.member.id = :memberId and ci.item.id = :itemId")
    Optional<CartItem> findByMemberAndItem(@Param("memberId") Long memberId, @Param("itemId") Long itemId);

    List<CartItem> findAllByMemberId(Long memberId);

    @Query("select ci from CartItem ci join fetch ci.item where ci.member.id = :memberId")
    List<CartItem> findAllByMemberIdWithItem(@Param("memberId") Long memberId);
}
