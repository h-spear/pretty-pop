package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.MemberNicknameRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/nickname")
    public ApiResponse modifyNickname(@Login Long id,
                                      @RequestBody MemberNicknameRequest nicknameRequest) {
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
