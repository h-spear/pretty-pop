package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.dto.MemberRegisterParam;
import prettypop.shop.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        log.info("회원가입 폼 페이지 이동");
        model.addAttribute("joinForm", new MemberRegisterParam());
        return "member/registerForm";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("joinForm") MemberRegisterParam memberRegisterParam) {
        log.info("회원가입 컨트롤러 실행");
        memberService.join(memberRegisterParam);
        return "redirect:/home";
    }
}
