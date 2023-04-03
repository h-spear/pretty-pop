package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.MemberNicknameRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.dto.member.MemberDto;
import prettypop.shop.dto.member.MemberRegisterParam;
import prettypop.shop.dto.member.MemberUpdateParam;
import prettypop.shop.exception.MemberEmailDuplicateException;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.exception.PasswordConfirmNotMatchException;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.MemberService;
import prettypop.shop.validation.ValidationSequence;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinForm", new MemberRegisterParam());
        return "member/registerForm";
    }

    @PostMapping("/join")
    public String join(@Validated(ValidationSequence.class) @ModelAttribute("joinForm") MemberRegisterParam memberRegisterParam,
                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/registerForm";
        }

        try {
            memberService.join(memberRegisterParam);
            return "redirect:/login";
        } catch (PasswordConfirmNotMatchException e) {
            bindingResult.rejectValue("passwordConfirm", "match");
        } catch (MemberUsernameDuplicateException e) {
            bindingResult.rejectValue("username", "duplicate");
        } catch (MemberEmailDuplicateException e) {
            bindingResult.rejectValue("email", "duplicate");
        }
        return "member/registerForm";
    }

    @GetMapping("/member")
    public String myPage(@Login Long id,
                         Model model) {
        model.addAttribute("memberInfo", memberService.getMemberInfo(id));
        return "member/myPage";
    }

    @GetMapping("/member/modify")
    public String modifyForm(@Login Long id,
                             Model model) {
        model.addAttribute("memberUpdateParam", getUpdateForm(id));
        return "member/memberModifyForm";
    }

    @PutMapping("/member")
    public String modify(@Login Long id,
                         @Validated(ValidationSequence.class) MemberUpdateParam memberUpdateParam,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "member/memberModifyForm";
        }

        try {
            memberService.update(id, memberUpdateParam);
            return "redirect:/member";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("error");
        }
        return "member/memberModifyForm";
    }

    @PutMapping("/member/nickname")
    @ResponseBody
    public ApiResponse modifyNickname(@Login Long id,
                                      @RequestBody MemberNicknameRequest changeRequest) {
        try {
            memberService.updateNickname(id, changeRequest.getNickname());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 회원입니다.");
        } catch (MemberNicknameDuplicateException e) {
            return ApiResponse.ofError("이미 존재하는 닉네임입니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @GetMapping("/wish")
    public String wish(@Login Long id,
                       Model model) {
        log.info("찜 목록 컨트롤러 id={}", id);
        model.addAttribute("wishList", itemService.getWishList(id));
        return "member/item/wishList";
    }

    @GetMapping("/cart")
    public String cart(@Login Long id,
                       Model model) {
        log.info("장바구니 컨트롤러 id={}", id);
        model.addAttribute("cartList", itemService.getCartList(id));
        return "member/item/shoppingCart";
    }

    private MemberUpdateParam getUpdateForm(Long id) {
        MemberDto memberInfo = memberService.getMemberInfo(id);
        MemberUpdateParam memberUpdateParam = new MemberUpdateParam();
        memberUpdateParam.setName(memberInfo.getName());
        memberUpdateParam.setAddress(memberInfo.getAddress());
        memberUpdateParam.setGender(memberInfo.getGender());
        memberUpdateParam.setEmail(memberInfo.getEmail());
        memberUpdateParam.setBirthDate(memberInfo.getBirthDate());
        memberUpdateParam.setPhoneNumber(memberInfo.getPhoneNumber());
        return memberUpdateParam;
    }
}