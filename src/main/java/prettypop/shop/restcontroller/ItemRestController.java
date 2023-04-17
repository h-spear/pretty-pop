package prettypop.shop.restcontroller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.restcontroller.request.ItemRequest;
import prettypop.shop.restcontroller.response.ApiResponse;
import prettypop.shop.service.ItemService;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "item", description = "상품")
@RestController
public class ItemRestController {

    private final ItemService itemService;

    @ApiOperation(value = "장바구니에 상품 추가")
    @PostMapping("/cart")
    public ApiResponse addCartItem(@Login Long memberId,
                                   @RequestBody ItemCountRequest itemRequest) {

        if (itemRequest.getQuantity() <= 0) {
            return ApiResponse.ofError("상품을 한 개이상 추가할 수 있습니다.");
        }

        try {
            itemService.addCartItem(memberId, itemRequest.getItemId(), itemRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("존재하지 않는 상품이거나 회원 오류가 발생했습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @ApiOperation(value = "장바구니 상품 개수 수정")
    @PutMapping("/cart")
    public ApiResponse updateCartItem(@Login Long memberId,
                                      @RequestBody ItemCountRequest itemRequest) {

        if (itemRequest.getQuantity() <= 0) {
            return ApiResponse.ofError("상품 개수는 한 개이상이어야 합니다.");
        }

        try {
            itemService.updateCartItemQuantity(memberId, itemRequest.getItemId(), itemRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호, 상품 번호 혹은 개수가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @ApiOperation(value = "장바구니 상품 삭제")
    @DeleteMapping("/cart")
    public ApiResponse deleteCartItems(@Login Long memberId,
                                       @RequestBody ItemRequest itemRequest) {
        itemService.deleteCartItem(memberId, itemRequest.getItemId());
        return ApiResponse.ofSuccess();
    }

    @ApiOperation(value = "찜 목록에 상품 추가")
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

    @ApiOperation(value = "찜 목록 상품 삭제")
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
