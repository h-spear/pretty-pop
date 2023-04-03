package prettypop.shop.dto.review;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Review;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewDto {

    private Long id;

    private Long itemId;
    private String itemImageUrl;

    private int rating;
    private String content;
    private LocalDateTime registrationDate;
    private LocalDateTime lastModifiedDate;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .itemId(review.getItem().getId())
                .itemImageUrl(review.getItem().getThumbnailImageUrl())
                .rating(review.getRating())
                .content(review.getContent())
                .registrationDate(review.getRegistrationDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .build();
    }
}
