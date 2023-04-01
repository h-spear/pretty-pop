package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.dto.MemberRegisterParam;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final ItemService itemService;

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

    @GetMapping("/my-page")
    public String myPage(@Login Long id,
                         Model model) {
        log.info("마이페이지 컨트롤러 id={}", id);
        model.addAttribute("memberInfo", memberService.getMemberInfo(id));
        return "member/myPage";
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
}
