package prettypop.shop.controller.request;

import lombok.*;
import prettypop.shop.validation.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;

@Data
public class MemberNicknameRequest {

    @NotBlank(message = "닉네임을 공백으로 할 수 없습니다.")
    private String nickname;

    @Size(min=4, max=16, message = "닉네임은 4글자 ~ 16글자로 작성해주세요. (한글 1글자 = 영어 2글자)")
    public byte[] getNicknameByteLength() throws UnsupportedEncodingException {
        return nickname.getBytes("EUC-KR");
    }
}
