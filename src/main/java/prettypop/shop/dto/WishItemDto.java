package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;

@Data
@Builder
public class WishItemDto {

    private Long id;
    private String itemName;

    private int originalPrice;
    private int purchasePrice;

    private int discountRate;

    private String thumbnailImageUrl;

    public static WishItemDto of(Item item) {
        return WishItemDto.builder()
                .id(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .discountRate((int) ((double) (item.getOriginalPrice() - item.getPurchasePrice()) * 100 / (double) item.getOriginalPrice()))
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .build();
    }
}
