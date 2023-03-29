package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.configuration.jwt.JwtTokenProvider;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.service.auth.AuthService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test...";
    }

    @GetMapping("/user/logic")
    public String user() {
        return "user!";
    }

    @GetMapping("/admin/logic")
    public String admin() {
        return "admin user!";
    }
}
