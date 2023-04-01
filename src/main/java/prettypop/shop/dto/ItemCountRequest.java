package prettypop.shop.dto;

import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemCountRequest {

    private Long itemId;
    private int quantity;
}
