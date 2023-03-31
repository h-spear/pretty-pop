package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.CartItem;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("select ci from CartItem ci where ci.member = :member and ci.item = :item")
    Optional<CartItem> findByMemberItem(@Param("member") Member member, @Param("item") Item item);
}
