package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.review.CanReviewItemDto;
import prettypop.shop.dto.review.ReviewDto;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.OrderItem;
import prettypop.shop.entity.Review;
import prettypop.shop.exception.CannotWriteReviewException;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.OrderItemRepository;
import prettypop.shop.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    public boolean canReview(Long memberId, Long itemId) {
        int notReviewedItemCount = orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId).size();
        return notReviewedItemCount > 0;
    }

    @Transactional
    public Long writeReview(Long memberId, Long itemId, int rating, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(IllegalArgumentException::new);

        int count = updateHasReviewTrue(memberId, itemId);
        if (count == 0) {
            throw new CannotWriteReviewException();
        }
        Review review = reviewRepository.save(new Review(item, rating, content, member));
        return review.getId();
    }

    public int updateHasReviewTrue(Long memberId, Long itemId) {
        List<OrderItem> notReviewedItem = orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId);
        int count = orderItemRepository.updateBulkHasReviewTrue(notReviewedItem);
        return count;
    }

    @Transactional
    public Long modifyReview(Long memberId, Long reviewId, int rating, String content) {
        Review review = reviewRepository.findByIdWithMember(reviewId)
                .orElseThrow(IllegalArgumentException::new);

        if (!review.getReviewer().getId().equals(memberId)) {
            throw new IllegalArgumentException();
        }

        review.setRating(rating);
        review.setContent(content);
        return review.getId();
    }

    @Transactional
    public void deleteReview(Long memberId, Long reviewId) {
        Review review = reviewRepository.findByIdWithMember(reviewId)
                .orElseThrow(IllegalArgumentException::new);

        if (!review.getReviewer().getId().equals(memberId)) {
            throw new IllegalArgumentException();
        }

        reviewRepository.delete(review);
    }

    public List<ReviewDto> findAllReviews(Long memberId) {
        return reviewRepository.findAllByReviewerIdWithItem(memberId).stream()
                .map(review -> ReviewDto.of(review))
                .collect(Collectors.toList());
    }

    public List<CanReviewItemDto> findCanReviewItems(Long memberId) {
        return orderItemRepository.findNotReviewedItem(memberId).stream()
                .map(item -> CanReviewItemDto.of(item))
                .collect(Collectors.toList());
    }
}
