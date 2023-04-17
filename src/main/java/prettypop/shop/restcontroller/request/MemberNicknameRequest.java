package prettypop.shop.restcontroller.request;

import lombok.*;
import prettypop.shop.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;

@Data
public class MemberNicknameRequest {

    @NotBlank(message = "{NotBlank.nickname}")
    private String nickname;

    @Size(min=4, max=16, message = "{Size.nickname}")
    public byte[] getNicknameByteLength() throws UnsupportedEncodingException {
        return nickname.getBytes("EUC-KR");
    }
}
