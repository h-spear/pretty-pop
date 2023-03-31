package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.configuration.security.SecurityContextUtils;
import prettypop.shop.configuration.security.User;
import prettypop.shop.controller.api.AddCartRequest;
import prettypop.shop.controller.api.ApiResponse;
import prettypop.shop.service.MemberService;

@Slf4j
@RequiredArgsConstructor
@RestController
@Controller
public class ApiController {

    private final SecurityContextUtils securityContextUtils;
    private final MemberService memberService;

    @PostMapping("/cart")
    public ApiResponse addCart(@ModelAttribute AddCartRequest addCartRequest) {
        Long itemId = addCartRequest.getItemId();
        int quantity = addCartRequest.getQuantity();

        if (quantity <= 0) {
            return ApiResponse.ofError("상품을 한 개이상 추가할 수 있습니다.");
        }

        User user = securityContextUtils.getPrincipal();
        try {
            memberService.addCart(user.getId(), itemId, quantity);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 아이템이거나 회원 오류가 발생했습니다.");
        } finally {
            log.info("카트에 아이템 추가 memberId={}, itemId={}, quantity={}", user.getId(), itemId, quantity);
        }
        return ApiResponse.ofSuccess();
    }
}
