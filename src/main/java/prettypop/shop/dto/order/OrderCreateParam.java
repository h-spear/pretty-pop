package prettypop.shop.dto.order;

import lombok.Data;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.entity.Address;
import prettypop.shop.validation.ValidationGroups;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class OrderCreateParam {

    @Valid
    @NotNull
    @Size(min = 1)
    private List<ItemCountRequest> orderItemRequests;
    private int paymentAmount;
    private int deliveryFee;
    private int earnedPoint;
    private int usedPoint;

    @NotBlank
    @Size(min=4, max=32)
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$")
    private String recipientContact;

    @Valid
    @NotNull
    private Address recipientAddress;

    private String memo;
}
