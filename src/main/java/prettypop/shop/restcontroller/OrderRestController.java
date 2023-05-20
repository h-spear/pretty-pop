package prettypop.shop.restcontroller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.member.MemberRegisterParam;
import prettypop.shop.dto.order.OrderCreateParam;
import prettypop.shop.restcontroller.response.ApiResponse;
import prettypop.shop.service.MemberService;
import prettypop.shop.service.OrderService;
import prettypop.shop.utils.CookieConst;
import prettypop.shop.utils.JsonUtils;
import prettypop.shop.validation.ValidationSequence;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "order", description = "주문")
@RestController
@RequestMapping("/order")
public class OrderRestController {

    private final MemberService memberService;
    private final OrderService orderService;
    private final MessageSource messageSource;

    @ApiOperation(value = "상품 주문")
    @PostMapping
    public ApiResponse createOrder(@Login Long id,
                                   @Validated(ValidationSequence.class) @RequestBody OrderCreateParam orderCreateParam,
                                   BindingResult bindingResult,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        if (id == null) {
            orderCreateParam.setUsedPoint(orderCreateParam.getPaymentAmount());
            orderCreateParam.setPaymentAmount(0);
        } else {
            // 결제를 구현하지 않기 때문에 모든 포인트를 사용해야만 결제가 되도록 함
            if (orderCreateParam.getPaymentAmount() != 0) {
                return ApiResponse.ofError(messageSource.getMessage("fail.notEnoughMoney", null, null));
            }
        }

        for (FieldError error: bindingResult.getFieldErrors()) {
            return ApiResponse.ofError(error.getDefaultMessage());
        }
        if (bindingResult.hasErrors()) {
            return ApiResponse.ofError(messageSource.getMessage("error", null, null));
        }
        Long orderId;
        try {
            if (id == null) {
                String cookieValue = JsonUtils.getCookieValue(request, CookieConst.CART_ITEMS_COOKIE);
                Map<String, Object> objectMap = JsonUtils.jsonToMap(cookieValue);
                for (ItemCountRequest req: orderCreateParam.getOrderItemRequests()) {
                    objectMap.remove(req.getItemId().toString());   // 장바구니 상품 제거
                }
                JsonUtils.addCookie(response, CookieConst.CART_ITEMS_COOKIE, JsonUtils.mapToJson(objectMap));
                id = memberService.createDummyMember();
            }
            orderId = orderService.createOrder(id, orderCreateParam);
            orderService.completeDelivery(orderId);   // 주문을 하자마자 배송완료 처리
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError(messageSource.getMessage("fail.payment", null, null));
        }
        return ApiResponse.ofSuccess(orderId);
    }

}
