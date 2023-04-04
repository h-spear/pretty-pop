package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.jwt.JwtTokenUtils;
import prettypop.shop.configuration.jwt.TokenConst;
import prettypop.shop.configuration.security.SecurityContextUtils;
import prettypop.shop.dto.auth.LoginParam;
import prettypop.shop.dto.auth.Token;
import prettypop.shop.exception.LoginPasswordNotMatchException;
import prettypop.shop.service.auth.AuthService;
import prettypop.shop.validation.ValidationSequence;

import javax.security.auth.RefreshFailedException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtils jwtTokenUtils;
    private final SecurityContextUtils securityContextUtils;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginParam());
        return "member/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated(ValidationSequence.class) @ModelAttribute("loginForm") LoginParam loginParam,
                        @RequestParam(defaultValue = "/home") String redirectURL,
                        BindingResult bindingResult,
                        HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "member/loginForm";
        }

        try {
            Token token = authService.login(loginParam);
            jwtTokenUtils.setCookieAccessToken(response, token.getAccessToken());
            jwtTokenUtils.setCookieRefreshToken(response, token.getRefreshToken());
            securityContextUtils.setAuthentication(token.getAccessToken());
            return "redirect:" + redirectURL;
        } catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("username", "notFound");
        } catch (LoginPasswordNotMatchException e) {
            bindingResult.rejectValue("password", "match");
        }

        return "member/loginForm";
    }

    @RequestMapping("/refresh")
    public String validateRefreshToken(@RequestParam(defaultValue = "/home") String redirectURL,
                                       @CookieValue(value = TokenConst.REFRESH_TOKEN) Cookie cookie,
                                       HttpServletResponse response) {
        log.info("리프레시 컨트롤러 실행");
        String refreshToken = cookie.getValue();
        String newAccessToken;

        try {
            newAccessToken = authService.reissueAccessToken(refreshToken);
        } catch (RefreshFailedException e) {
            log.info("액세스 토큰 신규 발급 실패 로그인 리다이렉트. 리프레시 토큰={}", refreshToken);
            log.info("{}", response.getStatus());
            return "redirect:/login?redirectURL=" + redirectURL;
        }

        log.info("액세스 토큰 신규 발급={}", newAccessToken);
        jwtTokenUtils.setCookieAccessToken(response, newAccessToken);
        securityContextUtils.setAuthentication(newAccessToken);
        return "redirect:" + redirectURL;
    }
}