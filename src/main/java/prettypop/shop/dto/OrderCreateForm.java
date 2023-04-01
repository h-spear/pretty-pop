package prettypop.shop.dto;

import lombok.Data;
import prettypop.shop.entity.Address;

import java.util.List;

@Data
public class OrderCreateForm {

    private List<OrderItemDto> orderItemDtos;
    private int earnedPoint;
    private int userPoint;
    private int totalItemPrice;

    private String recipientName;
    private String recipientContact;
    private Address recipientAddress;
    private String memo;
}
