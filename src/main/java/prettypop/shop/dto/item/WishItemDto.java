package prettypop.shop.dto.item;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.WishItem;

@Data
@Builder
public class WishItemDto {

    private Long itemId;
    private String itemName;

    private int originalPrice;
    private int purchasePrice;

    private int discountRate;

    private String thumbnailImageUrl;

    public static WishItemDto of(WishItem wishItem) {
        Item item = wishItem.getItem();
        return WishItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .discountRate((int) ((double) (item.getOriginalPrice() - item.getPurchasePrice()) * 100 / (double) item.getOriginalPrice()))
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .build();
    }
}
