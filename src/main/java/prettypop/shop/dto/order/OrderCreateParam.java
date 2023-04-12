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
import java.io.UnsupportedEncodingException;
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

    @NotBlank(message = "{NotBlank.recipientName}", groups = ValidationGroups.NotBlankGroup.class)
    private String recipientName;

    @NotBlank(message = "{NotBlank.recipientContact}", groups = ValidationGroups.NotBlankGroup.class)
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
             message = "{Pattern.recipientContact}", groups = ValidationGroups.PatternCheckGroup.class)
    private String recipientContact;

    private Address recipientAddress;

    private String memo;

    @Size(min=4, max=32, message = "{Size.recipientName}", groups = ValidationGroups.SizeCheckGroup.class)
    public byte[] getNameByteLength() throws UnsupportedEncodingException {
        return recipientName.getBytes("EUC-KR");
    }

    @NotBlank(message = "{NotBlank.recipientAddress}", groups = ValidationGroups.NotBlankGroup.class)
    public String getZipcode() {
        return recipientAddress.getZipcode();
    }

    @NotBlank(message = "{NotBlank.recipientAddress}", groups = ValidationGroups.NotBlankGroup.class)
    public String getJibunAddress() {
        return recipientAddress.getJibunAddress();
    }
}
