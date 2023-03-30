package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prettypop.shop.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
