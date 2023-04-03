package prettypop.shop.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
public class Address {

    @NotBlank
    private String zipcode;

    @NotBlank
    private String address;

    private String jibunAddress;
    private String detailAddress;

    protected Address() {
    }

    public Address(String zipcode, String address, String jibunAddress, String detailAddress) {
        this.zipcode = zipcode;
        this.address = address;
        this.jibunAddress = jibunAddress;
        this.detailAddress = detailAddress;
    }
}
