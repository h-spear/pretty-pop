package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.WishItem;

import java.util.List;
import java.util.Optional;

public interface WishItemRepository extends JpaRepository<WishItem, Long> {

    @Query("select wi from WishItem wi where wi.member.id = :memberId and wi.item.id = :itemId")
    Optional<WishItem> findByMemberAndItem(@Param("memberId") Long memberId, @Param("itemId") Long itemId);

    List<WishItem> findByMemberId(Long memberId);
}
