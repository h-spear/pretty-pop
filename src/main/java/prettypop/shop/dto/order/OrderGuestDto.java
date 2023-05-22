package prettypop.shop.dto.order;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderGuestDto {

    private Long id;
    private List<OrderItemDto> orderItemDtos;

    private int totalItemPrice;
    private int paymentAmount;
    private int deliveryFee;

    private String recipientName;
    private String recipientContact;
    private Address recipientAddress;
    private String memo;

    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;

    private LocalDateTime orderDate;

    public static OrderGuestDto of(Order order) {
        Delivery delivery = order.getDelivery();

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                .map(orderItem -> OrderItemDto.of(orderItem.getItem(), orderItem.getCount()))
                .collect(Collectors.toList());

        int totalItemPrice = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getItem().getPurchasePrice() * orderItem.getCount())
                .mapToInt(Integer::new)
                .sum();

        return OrderGuestDto.builder()
                .id(order.getId())
                .orderItemDtos(orderItemDtos)
                .totalItemPrice(totalItemPrice)
                .paymentAmount(order.getPaymentAmount())
                .deliveryFee(order.getDeliveryFee())
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
