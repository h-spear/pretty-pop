package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.MemberNicknameRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.service.MemberService;
import prettypop.shop.validation.ValidationSequence;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberApiController {

    private final MemberService memberService;

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
