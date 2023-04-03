package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.order.OrderCreateParam;
import prettypop.shop.dto.order.OrderDto;
import prettypop.shop.dto.order.OrderSimpleDto;
import prettypop.shop.entity.*;
import prettypop.shop.repository.*;

import java.util.ArrayList;
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
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Long createOrder(Long memberId, OrderCreateParam orderCreateParam) {
        Map<Long, Integer> quantityMap = generateQuantityMap(orderCreateParam);
        List<Long> ids = generateIds(orderCreateParam);

        // 회원 포인트 사용
        Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
        member.decreasePoint(orderCreateParam.getUsedPoint());

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

        // 주문에 성공하면 장바구니에서 제거
        List<Long> deleteIds = new ArrayList<>();
        List<CartItem> cartItems = cartItemRepository.findAllByMemberIdWithItem(memberId);
        cartItems.stream()
                .filter(cartItem -> quantityMap.containsKey(cartItem.getItem().getId()))
                .forEach(cartItem -> {
                    cartItem.removeCount(quantityMap.get(cartItem.getItem().getId()));
                    if (cartItem.getCount() <= 0) {
                        deleteIds.add(cartItem.getId());
                    }
                });
        cartItemRepository.deleteBulkById(deleteIds);

        return savedOrder.getId();
    }

    @Transactional
    public void completeDelivery(Long orderId) {
        Order order = orderRepository.findByIdWithFetchJoin(orderId)
                .orElseThrow(IllegalArgumentException::new);
        order.completeOrder();
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

    private List<Long> generateIds(OrderCreateParam orderCreateParam) {
        return orderCreateParam.getOrderItemRequests().stream()
                .map(ItemCountRequest::getItemId)
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> generateQuantityMap(OrderCreateParam orderCreateParam) {
        return orderCreateParam.getOrderItemRequests().stream()
                .collect(Collectors.toMap(
                        ItemCountRequest::getItemId,
                        ItemCountRequest::getQuantity
                ));
    }
}
