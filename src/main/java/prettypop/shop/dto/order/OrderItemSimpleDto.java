package prettypop.shop.dto.order;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.OrderItem;

@Data
@Builder
public class OrderItemSimpleDto {

    private Long itemId;
    private String itemName;
    private int purchasePrice;
    private int quantity;
    private String thumbnailImageUrl;

    public static OrderItemSimpleDto of(OrderItem orderItem) {
        return OrderItemSimpleDto.builder()
                .itemId(orderItem.getItem().getId())
                .itemName(orderItem.getItem().getName())
                .purchasePrice(orderItem.getItem().getPurchasePrice())
                .quantity(orderItem.getCount())
                .thumbnailImageUrl(orderItem.getItem().getThumbnailImageUrl())
                .build();
    }
}
