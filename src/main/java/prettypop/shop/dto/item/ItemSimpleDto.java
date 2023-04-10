package prettypop.shop.dto.item;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.ItemStatus;

@Data
@Builder
public class ItemSimpleDto {

    private Long id;
    private String name;

    private String thumbnailImageUrl;

    private int originalPrice;
    private int purchasePrice;
    private int earnedPoint;
    private int stockQuantity;
    private ItemStatus itemStatus;
    private Category category;

    public static ItemSimpleDto of(Item item) {
        return ItemSimpleDto.builder()
                .id(item.getId())
                .name(item.getName())
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .earnedPoint(item.getEarnedPoint())
                .stockQuantity(item.getStockQuantity())
                .itemStatus(item.getItemStatus())
                .category(item.getCategory())
                .build();
    }
}
