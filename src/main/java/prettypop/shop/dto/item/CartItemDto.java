package prettypop.shop.dto.item;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.CartItem;
import prettypop.shop.entity.Item;

@Data
@Builder
public class CartItemDto {

    private Long itemId;
    private String itemName;

    private int originalPrice;
    private int purchasePrice;
    private int discountRate;
    private int count;

    private String thumbnailImageUrl;

    public static CartItemDto of(CartItem cartItem) {
        Item item = cartItem.getItem();

        return CartItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .discountRate((int) ((double) (item.getOriginalPrice() - item.getPurchasePrice()) * 100 / (double) item.getOriginalPrice()))
                .count(cartItem.getCount())
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .build();
    }

    public static CartItemDto of(Item item, int count) {
        return CartItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .discountRate((int) ((double) (item.getOriginalPrice() - item.getPurchasePrice()) * 100 / (double) item.getOriginalPrice()))
                .count(count)
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .build();
    }
}
