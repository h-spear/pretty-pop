package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.ItemCountRequest;
import prettypop.shop.dto.OrderCreateParam;
import prettypop.shop.dto.OrderDto;
import prettypop.shop.dto.OrderSimpleDto;
import prettypop.shop.entity.Delivery;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.Order;
import prettypop.shop.entity.OrderItem;
import prettypop.shop.repository.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Long createOrder(Long memberId, OrderCreateParam orderCreateParam) {
        Map<Long, Integer> quantityMap = generateQuantityMap(orderCreateParam);
        List<Long> ids = generateIds(orderCreateParam);

        // 회원 포인트 사용
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        member.decreasePoint(orderCreateParam.getUsedPoint());
        member.increasePoint(orderCreateParam.getEarnedPoint());

        // 배송 정보
        Delivery delivery = Delivery.builder()
                .recipient(orderCreateParam.getRecipientName())
                .contact(orderCreateParam.getRecipientContact())
                .address(orderCreateParam.getRecipientAddress())
                .memo(orderCreateParam.getMemo())
                .build();
        deliveryRepository.save(delivery);

        // 주문 상품
        List<OrderItem> orderItems = itemRepository.findAllById(ids).stream()
                .map(item -> OrderItem.createOrderItem(item, quantityMap.get(item.getId())))
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

        // 주문 정보
        Order order = Order.builder()
                .member(member)
                .orderItems(orderItems)
                .earnedPoint(orderCreateParam.getEarnedPoint())
                .usedPoint(orderCreateParam.getUsedPoint())
                .deliveryFee(orderCreateParam.getDeliveryFee())
                .paymentAmount(orderCreateParam.getPaymentAmount())
                .delivery(delivery)
                .build();
        Order savedOrder = orderRepository.save(order);

        return savedOrder.getId();
    }

    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findByIdWithFetchJoin(orderId)
                .orElseThrow(IllegalArgumentException::new);
        return OrderDto.of(order);
    }

    public List<OrderSimpleDto> getOrdersByMemberAndDate(Long memberId, int year, int month) {
        return orderRepository.findAllByMemberAndOrderDate(memberId, year, month).stream()
                .map(order -> OrderSimpleDto.of(order))
                .collect(Collectors.toList());
    }

    private static List<Long> generateIds(OrderCreateParam orderCreateParam) {
        return orderCreateParam.getOrderItemRequests().stream()
                .map(ItemCountRequest::getItemId)
                .collect(Collectors.toList());
    }

    private static Map<Long, Integer> generateQuantityMap(OrderCreateParam orderCreateParam) {
        return orderCreateParam.getOrderItemRequests().stream()
                .collect(Collectors.toMap(
                        ItemCountRequest::getItemId,
                        ItemCountRequest::getQuantity
                ));
    }
}
