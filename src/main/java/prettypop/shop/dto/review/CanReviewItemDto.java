package prettypop.shop.dto.review;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Item;

@Data
@Builder
public class CanReviewItemDto {

    private Long itemId;
    private String itemName;
    private String itemImageUrl;

    public static CanReviewItemDto of(Item item) {
        return CanReviewItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .itemImageUrl(item.getThumbnailImageUrl())
                .build();
    }
}
