package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.service.ItemService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping({"/", "/home"})
    public String home(@Login Long id) {
        log.info("login user id={}", id);
        return "home";
    }

    @GetMapping("/my-page")
    public String myPage(@Login Long id) {
        log.info("마이페이지 컨트롤러 id={}", id);
        return "redirect:/member/" + id;
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

