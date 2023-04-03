package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.ItemStatus;
import prettypop.shop.entity.Review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;

    private String name;
    private String description;

    private int originalPrice;
    private int purchasePrice;
    private int earnedPoint;

    private int stockQuantity;
    private int salesVolume;

    private ItemStatus itemStatus;
    private Category category;

    private double rating;
    private int reviewCount;

    private String thumbnailImageUrl;
    private List<String> itemFileUrls;
    private List<ItemReviewDto> reviewDtos;

    public static ItemDto of(Item item) {
        int reviewRatingSum = 0;
        int reviewCount = item.getReviews().size();

        List<ItemReviewDto> reviewDtos = new ArrayList<>();
        for (Review review: item.getReviews()) {
            reviewDtos.add(ItemReviewDto.of(review));
            reviewRatingSum += review.getRating();
        }
        Collections.sort(reviewDtos);

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .earnedPoint(item.getEarnedPoint())
                .stockQuantity(item.getStockQuantity())
                .salesVolume(item.getSalesVolume())
                .itemStatus(item.getItemStatus())
                .category(item.getCategory())
                .rating((double) reviewRatingSum / (double) reviewCount)
                .reviewCount(reviewCount)
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .itemFileUrls(item.getImageFileUrls())
                .reviewDtos(reviewDtos)
                .build();
    }

    public int getDiscountRate() {
        return (int) ((double) (originalPrice - purchasePrice) * 100 / (double) originalPrice);
    }
}
