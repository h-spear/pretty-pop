package prettypop.shop.dto.auth;

import lombok.Data;
import prettypop.shop.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginParam {

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Size(min=4, max=16, groups = ValidationGroups.SizeCheckGroup.class)
    @Pattern(regexp = "[a-zA-Z0-9]{4,16}",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String username;

    @NotBlank(groups = ValidationGroups.NotBlankGroup.class)
    @Size(min=8, max=24, groups = ValidationGroups.SizeCheckGroup.class)
    private String password;
}
