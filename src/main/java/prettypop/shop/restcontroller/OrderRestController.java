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
import prettypop.shop.dto.order.OrderCreateParam;
import prettypop.shop.restcontroller.response.ApiResponse;
import prettypop.shop.service.OrderService;
import prettypop.shop.validation.ValidationSequence;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "order", description = "주문")
@RestController
@RequestMapping("/order")
public class OrderRestController {

    private final OrderService orderService;
    private final MessageSource messageSource;

    @ApiOperation(value = "상품 주문")
    @PostMapping
    public ApiResponse createOrder(@Login Long id,
                                   @Validated(ValidationSequence.class) @RequestBody OrderCreateParam orderCreateParam,
                                   BindingResult bindingResult) {
        // 결제를 구현하지 않기 때문에 모든 포인트를 사용해야만 결제가 되도록 함
        if (orderCreateParam.getPaymentAmount() != 0) {
            return ApiResponse.ofError(messageSource.getMessage("fail.notEnoughMoney", null, null));
        }

        for (FieldError error: bindingResult.getFieldErrors()) {
            return ApiResponse.ofError(error.getDefaultMessage());
        }
        if (bindingResult.hasErrors()) {
            return ApiResponse.ofError(messageSource.getMessage("error", null, null));
        }

        Long orderId;
        try {
            orderId = orderService.createOrder(id, orderCreateParam);

            // 학습용 홈페이지이므로 주문을 하자마자 배송완료 처리
            orderService.completeDelivery(orderId);
        } catch (IllegalArgumentException e) {

            return ApiResponse.ofError(messageSource.getMessage("fail.payment", null, null));
        }
        return ApiResponse.ofSuccess(orderId);
    }

}
