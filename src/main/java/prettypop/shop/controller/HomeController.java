package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.MemberService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/notice")
    public String notice() {
        return "notice";
    }

    @GetMapping("/cs-center")
    public String csCenter() {
        return "csCenter";
    }

    @GetMapping("/language")
    public String languageChange(HttpServletRequest request,
                                 @CookieValue(value = "lang", defaultValue = "ko") String language) {
        String requestURI = request.getRequestURI();
        if (language.equals("en")) {
            return "redirect:/home?lang=ko";
        }
        return "redirect:/home?lang=en";
    }
}

