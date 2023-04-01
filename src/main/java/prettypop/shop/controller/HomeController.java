package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.MemberService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(@Login Long id) {
        log.info("login user id={}", id);
        return "home";
    }
}

