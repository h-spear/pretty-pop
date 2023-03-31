package prettypop.shop.controller.api;

import lombok.Data;

@Data
public class AddCartRequest {
    private Long itemId;
    private int quantity;
}
