package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.api.ApiResponse;
import prettypop.shop.service.ItemService;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ItemService itemService;

    @PostMapping("/cart")
    public ApiResponse addCartItem(@Login Long memberId,
                                   @RequestParam Long itemId,
                                   @RequestParam int quantity) {

        if (quantity <= 0) {
            return ApiResponse.ofError("상품을 한 개이상 추가할 수 있습니다.");
        }

        try {
            itemService.addCartItem(memberId, itemId, quantity);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 상품이거나 회원 오류가 발생했습니다.");
        } finally {
            log.info("카트에 아이템 추가 memberId={}, itemId={}, quantity={}", memberId, itemId, quantity);
        }
        return ApiResponse.ofSuccess();
    }

    @PutMapping("/cart")
    public ApiResponse updateCartItem(@Login Long memberId,
                                      @RequestParam Long itemId,
                                      @RequestParam int quantity) {

        if (quantity <= 0) {
            return ApiResponse.ofError("상품 개수는 한 개이상이어야 합니다.");
        }

        try {
            itemService.updateCartItemQuantity(memberId, itemId, quantity);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호, 상품 번호 혹은 개수가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("/cart")
    public ApiResponse deleteCartItems(@Login Long memberId,
                                       @RequestParam Long itemId) {
        itemService.deleteCartItem(memberId, itemId);
        return ApiResponse.ofSuccess();
    }

    @PostMapping("/wish")
    public ApiResponse addWish(@Login Long memberId,
                               @RequestParam Long itemId) {
        try {
            itemService.addWish(memberId, itemId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 아이템이거나 회원 오류가 발생했습니다.");
        } finally {
            log.info("찜 목록에 아이템 추가 memberId={}, itemId={}", memberId, itemId);
        }
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("/wish")
    public ApiResponse deleteWish(@Login Long memberId,
                                  @RequestParam Long itemId) {
        try {
            itemService.deleteWish(memberId, itemId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 상품이거나 회원 오류가 발생했습니다.");
        } finally {
            log.info("찜 목록의 아이템 삭제 memberId={}, itemId={}", memberId, itemId);
        }
        return ApiResponse.ofSuccess();
    }
}
