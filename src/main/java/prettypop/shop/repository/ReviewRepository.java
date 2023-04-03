package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import prettypop.shop.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r join fetch r.reviewer where r.id = :id")
    Optional<Review> findByIdWithMember(@Param("id") Long id);

    @Query("select r from Review r join fetch r.item where r.reviewer.id = :reviewerId")
    List<Review> findAllByReviewerIdWithItem(@Param("reviewerId") Long reviewerId);
}
