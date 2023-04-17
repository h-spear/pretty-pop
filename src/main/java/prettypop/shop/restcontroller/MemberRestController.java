package prettypop.shop.restcontroller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.restcontroller.request.MemberNicknameRequest;
import prettypop.shop.restcontroller.response.ApiResponse;
import prettypop.shop.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "member", description = "회원")
@RestController
@RequestMapping("/member")
public class MemberRestController {

    private final MemberService memberService;

    @ApiOperation(value = "닉네임 변경")
    @PutMapping("/nickname")
    public ApiResponse modifyNickname(@Login Long id,
                                      @RequestBody @Validated MemberNicknameRequest nicknameRequest,
                                      BindingResult bindingResult) {

        for (FieldError error: bindingResult.getFieldErrors()) {
            return ApiResponse.ofError(error.getDefaultMessage());
        }
        try {
            memberService.updateNickname(id, nicknameRequest.getNickname());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 회원입니다.");
        } catch (MemberNicknameDuplicateException e) {
            return ApiResponse.ofError("이미 존재하는 닉네임입니다.");
        }
        return ApiResponse.ofSuccess();
    }
}
