package prettypop.shop.dto.member;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MemberUpdateParam {

    private String name;
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private Address address;
    private String phoneNumber;
    private String email;
}
