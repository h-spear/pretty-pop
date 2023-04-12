package prettypop.shop.dto.member;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;
import prettypop.shop.validation.ValidationGroups;

import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

@Data
public class MemberRegisterParam {

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Size(min=4, max=16, groups = ValidationGroups.SizeCheckGroup.class)
    @Pattern(regexp = "[a-zA-Z0-9]{4,16}",
             groups = ValidationGroups.PatternCheckGroup.class)
    private String username;

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Size(min=8, max=24, groups = ValidationGroups.SizeCheckGroup.class)
    private String password;

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Size(min=8, max=24, groups = ValidationGroups.SizeCheckGroup.class)
    private String passwordConfirm;

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    private String name;

    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate = LocalDate.now();

    private Address address;

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
             groups = ValidationGroups.PatternCheckGroup.class)
    private String phoneNumber;

    private String email;

    @Size(min=4, max=32, groups = ValidationGroups.SizeCheckGroup.class)
    public byte[] getNameByteLength() throws UnsupportedEncodingException {
        return name.getBytes("EUC-KR");
    }

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    public String getZipcode() {
        return address.getZipcode();
    }

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    public String getJibunAddress() {
        return address.getJibunAddress();
    }
}
