package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.controller.request.CartItemRequest;
import prettypop.shop.controller.request.ItemRequest;
import prettypop.shop.service.ItemService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ApiController {

    private final ItemService itemService;

    @PostMapping("/cart")
    public ApiResponse addCartItem(@Login Long memberId,
                                   @RequestBody CartItemRequest cartItemRequest) {

        if (cartItemRequest.getQuantity() <= 0) {
            return ApiResponse.ofError("상품을 한 개이상 추가할 수 있습니다.");
        }

        try {
            itemService.addCartItem(memberId, cartItemRequest.getItemId(), cartItemRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 상품이거나 회원 오류가 발생했습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @PutMapping("/cart")
    public ApiResponse updateCartItem(@Login Long memberId,
                                      @RequestBody CartItemRequest cartItemRequest) {

        if (cartItemRequest.getQuantity() <= 0) {
            return ApiResponse.ofError("상품 개수는 한 개이상이어야 합니다.");
        }

        try {
            itemService.updateCartItemQuantity(memberId, cartItemRequest.getItemId(), cartItemRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호, 상품 번호 혹은 개수가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("/cart")
    public ApiResponse deleteCartItems(@Login Long memberId,
                                       @RequestBody ItemRequest itemRequest) {
        itemService.deleteCartItem(memberId, itemRequest.getItemId());
        return ApiResponse.ofSuccess();
    }

    @PostMapping("/wish")
    public ApiResponse addWish(@Login Long memberId,
                               @RequestBody ItemRequest itemRequest) {
        try {
            itemService.addWish(memberId, itemRequest.getItemId());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 아이템이거나 회원 오류가 발생했습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("/wish")
    public ApiResponse deleteWish(@Login Long memberId,
                                  @RequestBody ItemRequest itemRequest) {
        try {
            itemService.deleteWish(memberId, itemRequest.getItemId());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 상품이거나 회원 오류가 발생했습니다.");
        }
        return ApiResponse.ofSuccess();
    }
}
