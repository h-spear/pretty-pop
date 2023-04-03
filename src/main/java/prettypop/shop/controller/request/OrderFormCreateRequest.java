package prettypop.shop.controller.request;

import lombok.*;
import prettypop.shop.dto.item.ItemCountRequest;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class OrderFormCreateRequest {

    private List<ItemCountRequest> orderItems;
}
