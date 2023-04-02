package prettypop.shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;

import java.time.LocalDate;

@Data
public class MemberDto {

    private String username;
    private String name;
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String nickname;
    private Address address;
    private String phoneNumber;
    private String email;
    private int point;

    private int orderCount;
    private int reviewCount;

    @QueryProjection
    public MemberDto(String username, String name, Gender gender, LocalDate birthDate, String nickname, Address address, String phoneNumber, String email, int point, int orderCount, int reviewCount) {
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nickname = nickname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.point = point;
        this.orderCount = orderCount;
        this.reviewCount = reviewCount;
    }
}
