package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        int notReviewedItemCount = orderItemRepository.getNotReviewedItem(memberId, itemId).size();
        return notReviewedItemCount > 0;
    }

    @Transactional
    public Long writeReview(Long memberId, Long itemId, int rating, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(IllegalArgumentException::new);

        List<OrderItem> notReviewedItem = orderItemRepository.getNotReviewedItem(memberId, itemId);
        if (notReviewedItem.size() <= 0) {
            throw new CannotWriteReviewException();
        }
        orderItemRepository.checkHasReview(notReviewedItem);
        Review review = reviewRepository.save(new Review(item, rating, content, member));
        return review.getId();
    }
}
