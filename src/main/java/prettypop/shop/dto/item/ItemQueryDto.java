package prettypop.shop.dto.item;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.ItemStatus;

@Data
public class ItemQueryDto {

    private Long id;
    private String name;

    private String thumbnailImageUrl;

    private int originalPrice;
    private int purchasePrice;
    private int earnedPoint;
    private int stockQuantity;
    private ItemStatus itemStatus;
    private Category category;

    private double rating;
    private int reviewCount;

    @QueryProjection
    public ItemQueryDto(Long id, String name, String thumbnailImageUrl, int originalPrice, int purchasePrice, int earnedPoint, int stockQuantity, ItemStatus itemStatus, Category category, double rating, int reviewCount) {
        this.id = id;
        this.name = name;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.originalPrice = originalPrice;
        this.purchasePrice = purchasePrice;
        this.earnedPoint = earnedPoint;
        this.stockQuantity = stockQuantity;
        this.itemStatus = itemStatus;
        this.category = category;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    public int getRatingIntegerPart() {
        return (int) rating;
    }

    public double getRatingDecimalPart() {
        return rating - getRatingIntegerPart();
    }
}
