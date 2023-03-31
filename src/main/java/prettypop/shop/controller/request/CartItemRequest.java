package prettypop.shop.controller.request;

import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartItemRequest {

    private Long itemId;
    private int quantity;
}
