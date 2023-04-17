package prettypop.shop.restcontroller.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemRequest {

    private Long itemId;
}
