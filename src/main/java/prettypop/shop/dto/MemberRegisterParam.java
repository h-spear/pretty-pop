package prettypop.shop.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;
import prettypop.shop.entity.Member;

import javax.persistence.Embedded;
import java.time.LocalDate;

@Data
public class MemberRegisterParam {

    private String username;
    private String password;
    private String passwordConfirm;

    private String name;
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Embedded
    private Address address;

    private String phoneNumber;
    private String email;

}
