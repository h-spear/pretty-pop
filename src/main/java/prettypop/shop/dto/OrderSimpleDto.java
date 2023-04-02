package prettypop.shop.dto;

import lombok.Builder;
import lombok.Data;
import prettypop.shop.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderSimpleDto {

    private Long id;
    private List<OrderItemSimpleDto> orderItems;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;

    public static OrderSimpleDto of(Order order) {
        List<OrderItemSimpleDto> orderItemDtos = order.getOrderItems().stream()
                .map(orderItem -> OrderItemSimpleDto.of(orderItem))
                .collect(Collectors.toList());

        return OrderSimpleDto.builder()
                .id(order.getId())
                .orderItems(orderItemDtos)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
