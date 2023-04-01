package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.Item;

@Data
@Builder
public class OrderItemDto {

    private Long itemId;
    private String itemName;

    private int originalPrice;
    private int purchasePrice;
    private int discountRate;
    private int earnedPoint;
    private int quantity;

    private String thumbnailImageUrl;

    public static OrderItemDto of(Item item, int quantity) {
        return OrderItemDto.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .originalPrice(item.getOriginalPrice())
                .purchasePrice(item.getPurchasePrice())
                .discountRate((int) ((double) (item.getOriginalPrice() - item.getPurchasePrice()) * 100 / (double) item.getOriginalPrice()))
                .earnedPoint(item.getEarnedPoint())
                .quantity(quantity)
                .thumbnailImageUrl(item.getThumbnailImageUrl())
                .build();
    }
}
