package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.WishItem;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    @Query("select m from Member m join fetch m.roles where m.username = :username")
    Optional<Member> findByUsernameWithRoles(@Param("username") String username);

    @Query("select wl from Member m join m.wishList wl" +
            " join fetch wl.item" +
            " where m.id = :id")
    List<WishItem> findWishList(@Param("id") Long id);
}
