package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.WishItem;

import java.util.Optional;

public interface WishItemRepository extends JpaRepository<WishItem, Long> {

    @Query("select wi from WishItem wi where wi.member = :member and wi.item = :item")
    Optional<WishItem> findByMemberItem(@Param("member") Member member, @Param("item") Item item);
}
