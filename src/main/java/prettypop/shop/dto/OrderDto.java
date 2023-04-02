package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderDto {

    private Long id;
    private Long ordererId;

    private List<OrderItemDto> orderItemDtos;

    private int totalItemPrice;
    private int paymentAmount;
    private int deliveryFee;
    private int earnedPoint;
    private int usedPoint;

    private String recipientName;
    private String recipientContact;
    private Address recipientAddress;
    private String memo;

    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;

    private LocalDateTime orderDate;

    public static OrderDto of(Order order) {
        Delivery delivery = order.getDelivery();

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(orderItem -> OrderItemDto.of(orderItem.getItem(), orderItem.getCount()))
                .collect(Collectors.toList());

        int totalItemPrice = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getItem().getPurchasePrice() * orderItem.getCount())
                .mapToInt(Integer::new)
                .sum();

        return OrderDto.builder()
                .id(order.getId())
                .ordererId(order.getMember().getId())
                .orderItemDtos(orderItemDtos)
                .totalItemPrice(totalItemPrice)
                .paymentAmount(order.getPaymentAmount())
                .deliveryFee(order.getDeliveryFee())
                .earnedPoint(order.getEarnedPoint())
                .usedPoint(order.getUsedPoint())
                .recipientName(delivery.getRecipient())
                .recipientContact(delivery.getContact())
                .recipientAddress(delivery.getAddress())
                .memo(delivery.getMemo())
                .orderStatus(order.getOrderStatus())
                .deliveryStatus(delivery.getDeliveryStatus())
                .orderDate(order.getOrderDate())
                .build();
    }
}
