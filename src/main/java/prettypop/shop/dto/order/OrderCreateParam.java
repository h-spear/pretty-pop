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

    @NotBlank
    private String recipientName;

    @NotBlank(message = "수취인 연락처를 입력해주세요.")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
             message = "잘못된 휴대전화 번호입니다.")
    private String recipientContact;

    private Address recipientAddress;

    private String memo;

    @Size(min=4, max=32, message = "이름은 영문 기준 4 ~ 32 글자만 허용됩니다. (한글 1글자 = 영어 2글자)")
    public byte[] getNameByteLength() throws UnsupportedEncodingException {
        return recipientName.getBytes("EUC-KR");
    }

    @NotBlank(message = "배송지 주소를 입력해주세요.")
    public String getZipcode() {
        return recipientAddress.getZipcode();
    }

    @NotBlank(message = "배송지 주소를 입력해주세요.")
    public String getJibunAddress() {
        return recipientAddress.getJibunAddress();
    }
}
