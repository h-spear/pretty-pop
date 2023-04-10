package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.Item;
import prettypop.shop.repository.custom.ItemRepositoryCustom;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @Query("select i from Item i" +
            " left join fetch i.reviews r" +
            " left join fetch r.reviewer" +
            " where i.id = :id")
    Optional<Item> findByIdWithReviews(@Param("id") Long id);
}
