package prettypop.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.DateRequest;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.order.*;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.service.ItemService;
import prettypop.shop.service.OrderService;

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

    @PostMapping("/guest/{orderId}")
    public String getGuestOrder(@PathVariable("orderId") Long orderId,
                                @RequestParam String password,
                                Model model) {

        try {
            OrderGuestDto orderGuestDto = orderService.getGuestOrder(orderId, password);
            model.addAttribute("order", orderGuestDto);
        } catch (IllegalArgumentException e) {
            return "redirect:/home";
        }
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
        List<ItemCountRequest> itemRequests = parseItemRequests(paramMap);
        List<OrderItemDto> orderItemDtos = itemService.getOrderItems(itemRequests);
        OrderCreateForm orderCreateForm = new OrderCreateForm();
        fillOrderItemInfo(orderCreateForm, orderItemDtos);
        if (id == null) {
            orderItemDtos.forEach(
                    orderItemDto -> orderItemDto.setEarnedPoint(0));
            orderCreateForm.setEarnedPoint(0);
        } else {
            Member member = memberRepository.findById(id).orElse(null);
            if (member == null) {
                return "redirect:/home";
            }
            fillOrderRecipientInfo(orderCreateForm, member);
        }
        model.addAttribute("orderCreateForm", orderCreateForm);
        return "shop/order/orderCreateForm";
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
