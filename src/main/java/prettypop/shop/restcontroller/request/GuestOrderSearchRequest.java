package prettypop.shop.restcontroller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestOrderSearchRequest {

    private Long id;
    private String password;
}
