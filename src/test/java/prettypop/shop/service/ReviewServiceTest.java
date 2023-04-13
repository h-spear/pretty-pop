package prettypop.shop.service;

import org.aspectj.weaver.ast.Or;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.OrderItem;
import prettypop.shop.entity.Review;
import prettypop.shop.exception.CannotWriteReviewException;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.OrderItemRepository;
import prettypop.shop.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ReviewServiceTest {

    @Mock ReviewRepository reviewRepository;
    @Mock MemberRepository memberRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock ItemRepository itemRepository;

    @InjectMocks
    ReviewService reviewService;

    final Long memberId = 1L;
    final Long itemId = 1L;
    Member member;
    Item item;
    List<OrderItem> orderItems;

    @BeforeEach
    void beforeEach() {
        initData();
    }

    @Test
    @DisplayName("회원이 상품을 주문한 적있고 리뷰를 작성한 적 없으면 리뷰 작성 여부 true륿 반환한다")
    void canReviewTest_true() throws Exception {
        // mock
        when(orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId)).thenReturn(orderItems);

        // when
        boolean bool = reviewService.canReview(memberId, itemId);

        // then
        verify(orderItemRepository, times(1)).findNotReviewedByMemberAndItem(memberId, itemId);
        assertThat(bool).isTrue();
    }

    @Test
    @DisplayName("회원이 상품을 주문한 적 없으면 리뷰 작성 여부 false를 반환한다")
    void canReviewTest_false() throws Exception {
        // mock
        when(orderItemRepository.findNotReviewedByMemberAndItem(memberId, 2L)).thenReturn(new ArrayList<>());

        // when
        boolean bool = reviewService.canReview(memberId, 2L);

        // then
        verify(orderItemRepository, times(1)).findNotReviewedByMemberAndItem(memberId, 2L);
        assertThat(bool).isFalse();
    }

    @Test
    @DisplayName("회원이 구매한 상품 리뷰 작성 상태를 모두 true로 한다.")
    void updateHasReviewTrue() throws Exception {
        // mock
        when(orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId))
                .thenReturn(orderItems);
        when(orderItemRepository.updateBulkHasReviewTrue(orderItems)).thenReturn(2);

        // when
        int count = reviewService.updateHasReviewTrue(memberId, itemId);

        // then
        verify(orderItemRepository, times(1)).findNotReviewedByMemberAndItem(memberId, itemId);
        verify(orderItemRepository, times(1)).updateBulkHasReviewTrue(orderItems);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("리뷰를 작성한다.")
    void writeReviewTest() throws Exception {
        // mock
        Review review = new Review(item, 5, "review...", member);
        ReflectionTestUtils.setField(review, "id", 99L);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId))
                .thenReturn(orderItems);
        when(orderItemRepository.updateBulkHasReviewTrue(orderItems)).thenReturn(2);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        Long savedReviewId = reviewService.writeReview(memberId, itemId, 5, "review...");

        // then
        assertThat(savedReviewId).isEqualTo(99);
        verify(memberRepository, times(1)).findById(memberId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(orderItemRepository, times(1)).findNotReviewedByMemberAndItem(memberId, itemId);
        verify(orderItemRepository, times(1)).updateBulkHasReviewTrue(orderItems);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰를 작성할 수 있는 회원이 아니라면 예외가 발생한다.")
    void writeReviewTest_CannotWriteReviewException() throws Exception {
        // mock
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId))
                .thenReturn(new ArrayList<>());
        when(orderItemRepository.updateBulkHasReviewTrue(orderItems)).thenReturn(0);

        // assert
        assertThatThrownBy(
                () -> reviewService.writeReview(memberId, itemId, 5, "review...")
        ).isInstanceOf(CannotWriteReviewException.class);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(orderItemRepository, times(1)).findNotReviewedByMemberAndItem(memberId, itemId);
        verify(orderItemRepository, times(1)).updateBulkHasReviewTrue(new ArrayList<>());
        verify(reviewRepository, times(0)).save(any(Review.class));
    }

    @Test
    @DisplayName("이미 작성된 리뷰를 수정한다.")
    void modifyReviewTest() throws Exception {
        // given
        Long reviewId = 10L;
        Review review = new Review(item, 3, "Review", member);
        ReflectionTestUtils.setField(review, "id", reviewId);

        int newRating = 5;
        String newContent = "review rewritten";

        // mock
        when(reviewRepository.findByIdWithMember(reviewId)).thenReturn(Optional.of(review));

        // when
        Long modifiedId = reviewService.modifyReview(memberId, reviewId, newRating, newContent);

        // then
        verify(reviewRepository, times(1)).findByIdWithMember(reviewId);
        assertThat(modifiedId).isEqualTo(reviewId);
        assertThat(review.getRating()).isEqualTo(newRating);
        assertThat(review.getContent()).isEqualTo(newContent);
    }

    @Test
    @DisplayName("이미 작성된 리뷰를 삭제한다.")
    void deleteReviewTest() throws Exception {
        // given
        Long reviewId = 10L;
        Review review = new Review(item, 3, "Review", member);
        ReflectionTestUtils.setField(review, "id", reviewId);

        // mock
        when(reviewRepository.findByIdWithMember(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewService.deleteReview(memberId, reviewId);

        // then
        verify(reviewRepository, times(1)).findByIdWithMember(reviewId);
        verify(reviewRepository, times(1)).delete(review);
    }


    private void initData() {
        Long fakeMemberId = 1L;
        member = Member.builder().username("testUser").build();
        ReflectionTestUtils.setField(member, "id", fakeMemberId);

        Long fakeItemId = 1L;
        item = Item.builder().name("testItem").stockQuantity(9999).build();
        ReflectionTestUtils.setField(item, "id", fakeItemId);

        orderItems = new ArrayList<>();
        orderItems.add(OrderItem.createOrderItem(item, 4));
        orderItems.add(OrderItem.createOrderItem(item, 5));
    }
}