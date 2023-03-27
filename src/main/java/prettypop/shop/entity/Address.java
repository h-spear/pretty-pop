package prettypop.shop.entity;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String zipcode;
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
