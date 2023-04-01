package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.OrderFormCreateRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.dto.*;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final int DELIVERY_FEE = 3000;

    private final OrderService orderService;
    private final ItemService itemService;
    private final MemberRepository memberRepository;

    @GetMapping("/form")
    public String createOrderForm(@Login Long id,
                                  Model model) {
        List<ItemCountRequest> list = new ArrayList<>();
        for (long i = 12; i <= 15; ++i) {
            list.add(new ItemCountRequest(i, 4));
        }
        OrderFormCreateRequest request = new OrderFormCreateRequest(list);
        List<ItemCountRequest> itemRequests = request.getOrderItems();

        //
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isEmpty()) {
            return "redirect:/home";
        }
        List<OrderItemDto> orderItemDtos = itemService.getOrderItems(itemRequests);
        OrderCreateForm orderCreateForm = new OrderCreateForm();
        fillOrderItemInfo(orderCreateForm, orderItemDtos);
        fillOrderRecipientInfo(orderCreateForm, optionalMember.get());
        model.addAttribute("orderCreateForm", orderCreateForm);
        return "shop/order/orderCreateForm";
    }

    @GetMapping("/{orderId}")
    public String getOrder(@Login Long id,
                           @PathVariable("orderId") Long orderId,
                           Model model) {
        OrderDto orderDto = orderService.getOrder(orderId);
        if (orderDto.getOrdererId() != id) {
            return "redirect:/home";
        }
        model.addAttribute("order", orderDto);
        return "shop/order/order";
    }

    @PostMapping
    @ResponseBody
    public ApiResponse createOrder(@Login Long id,
                                   @RequestBody OrderCreateParam orderCreateParam) {

        // 결제를 구현하지 않기 때문에 모든 포인트를 사용해야만 결제가 되도록 함
        if (orderCreateParam.getPaymentAmount() != 0) {
            return ApiResponse.ofError("결제가 완료되지 않았습니다.");
        }

        Long orderId;
        try {
            orderId = orderService.createOrder(id, orderCreateParam);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("결제 과정에서 오류가 발생했습니다.");
        }
        return ApiResponse.ofSuccess(orderId);
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
