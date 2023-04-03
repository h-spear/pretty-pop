package prettypop.shop.dto.item;

import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemCountRequest {

    private Long itemId;
    private int quantity;
}
