package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Review;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemReviewDto implements Comparable<ItemReviewDto> {

    private Long id;
    private int rating;
    private String content;

    private Long reviewerId;
    private String reviewerUsername;
    private String reviewerNickname;

    private LocalDateTime registrationDate;
    private LocalDateTime lastModifiedDate;

    public static ItemReviewDto of(Review review) {
        return ItemReviewDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .reviewerId(review.getReviewer().getId())
                .reviewerUsername(review.getReviewer().getUsername())
                .reviewerNickname(review.getReviewer().getNickname())
                .registrationDate(review.getRegistrationDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .build();
    }

    @Override
    public int compareTo(ItemReviewDto o2) {
        return o2.registrationDate.compareTo(this.registrationDate);
    }
}
