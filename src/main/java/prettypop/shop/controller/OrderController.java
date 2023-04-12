package prettypop.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.DateRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.order.OrderCreateForm;
import prettypop.shop.dto.order.OrderCreateParam;
import prettypop.shop.dto.order.OrderDto;
import prettypop.shop.dto.order.OrderItemDto;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.OrderService;
import prettypop.shop.validation.ValidationSequence;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/order")
public class OrderController {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final OrderService orderService;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final MessageSource messageSource;

    @GetMapping("/{orderId}")
    public String getOrder(@Login Long id,
                           @PathVariable("orderId") Long orderId,
                           Model model) {
        OrderDto orderDto = orderService.getOrder(orderId);
        if (!orderDto.getOrdererId().equals(id)) {
            return "redirect:/home";
        }
        model.addAttribute("order", orderDto);
        return "shop/order/order";
    }

    @GetMapping
    public String getOrderList(@Login Long id,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        log.info("year={}, month={}", year, month);
        if (year == null || month == null) {
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
        }
        model.addAttribute("orders", orderService.getOrdersByMemberAndDate(id, year, month));
        model.addAttribute("dateRequest", new DateRequest(year, month));
        return "member/order/orders";
    }

    @GetMapping("/form")
    public String createOrderForm(@Login Long id,
                                  @RequestParam Map<String, String> paramMap,
                                  Model model) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            return "redirect:/home";
        }
        List<ItemCountRequest> itemRequests = parseItemRequests(paramMap);
        List<OrderItemDto> orderItemDtos = itemService.getOrderItems(itemRequests);
        OrderCreateForm orderCreateForm = new OrderCreateForm();
        fillOrderItemInfo(orderCreateForm, orderItemDtos);
        fillOrderRecipientInfo(orderCreateForm, member);
        model.addAttribute("orderCreateForm", orderCreateForm);
        return "shop/order/orderCreateForm";
    }

    @PostMapping
    @ResponseBody
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

    private List<ItemCountRequest> parseItemRequests(Map<String, String> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> {
                    try {
                        return objectMapper.readValue(entry.getValue(), ItemCountRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private void fillOrderItemInfo(OrderCreateForm orderCreateForm, List<OrderItemDto> orderItemDtos) {
        int earnedPoint = 0;
        int totalItemPrice = 0;
        for (OrderItemDto orderItemDto: orderItemDtos) {
            earnedPoint += orderItemDto.getEarnedPoint() * orderItemDto.getQuantity();
            totalItemPrice += orderItemDto.getPurchasePrice() * orderItemDto.getQuantity();
        }
        orderCreateForm.setOrderItemDtos(orderItemDtos);
        orderCreateForm.setEarnedPoint(earnedPoint);
        orderCreateForm.setTotalItemPrice(totalItemPrice);
    }

    private void fillOrderRecipientInfo(OrderCreateForm orderCreateForm, Member member) {
        orderCreateForm.setRecipientName(member.getName());
        orderCreateForm.setRecipientContact(member.getPhoneNumber());
        orderCreateForm.setRecipientAddress(member.getAddress());
        orderCreateForm.setUserPoint(member.getPoint());
    }
}
