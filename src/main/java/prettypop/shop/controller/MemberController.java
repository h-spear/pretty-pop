package prettypop.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.dto.item.CartItemDto;
import prettypop.shop.dto.member.MemberDto;
import prettypop.shop.dto.member.MemberRegisterParam;
import prettypop.shop.dto.member.MemberUpdateParam;
import prettypop.shop.exception.MemberEmailDuplicateException;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.exception.PasswordConfirmNotMatchException;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.MemberService;
import prettypop.shop.utils.JsonUtils;
import prettypop.shop.validation.ValidationSequence;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (bindingResult.hasFieldErrors("nameByteLength")) {
            bindingResult.rejectValue("name", "Size", new Object[]{4, 32}, null);
        }
        if (bindingResult.hasFieldErrors("zipcode") || bindingResult.hasFieldErrors("zibunAddress")) {
            bindingResult.rejectValue("address", "NotBlank");
        }
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

        if (bindingResult.hasFieldErrors("nameByteLength")) {
            bindingResult.rejectValue("name", "Size", new Object[]{4, 32}, null);
        }
        if (bindingResult.hasFieldErrors("zipcode") || bindingResult.hasFieldErrors("zibunAddress")) {
            bindingResult.rejectValue("address", "NotBlank");
        }
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

    @GetMapping("/wish")
    public String wish(@Login Long id,
                       Model model) {
        log.info("찜 목록 컨트롤러 id={}", id);
        model.addAttribute("wishList", itemService.getWishList(id));
        return "member/item/wishList";
    }

    @GetMapping("/cart")
    public String cart(@Login Long id,
                       Model model,
                       @CookieValue(name = "cartItemCookie", required = false) Cookie cartItemCookie) throws JsonProcessingException {

        log.info("장바구니 컨트롤러 id={}", id);
        if (id == null) {
            if (cartItemCookie == null) {
                model.addAttribute("cartList", new ArrayList<>());
            } else {
                String json = JsonUtils.decodeJsonString(cartItemCookie.getValue());
                Map<String, Object> objectMap = JsonUtils.jsonToMap(json);
                Map<Long, Integer> itemQuantityMap = generateItemQuantityMap(objectMap);
                model.addAttribute("cartList", itemService.getCartListByMap(itemQuantityMap));
            }
        } else {
            model.addAttribute("cartList", itemService.getCartList(id));
        }
        return "member/item/shoppingCart";
    }

    private static Map<Long, Integer> generateItemQuantityMap(Map<String, Object> objectMap) {
        Map<Long, Integer> itemQuantityMap = new HashMap<>();
        for (Map.Entry<String, Object> entry: objectMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            itemQuantityMap.put(Long.parseLong(key), (Integer) value);
        }
        return itemQuantityMap;
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