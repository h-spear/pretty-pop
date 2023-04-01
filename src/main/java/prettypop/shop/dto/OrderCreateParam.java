package prettypop.shop.dto;

import lombok.Data;
import prettypop.shop.entity.Address;

import java.util.List;

@Data
public class OrderCreateParam {

    private List<ItemCountRequest> orderItemRequests;
    private int paymentAmount;
    private int deliveryFee;
    private int earnedPoint;
    private int usedPoint;

    private String recipientName;
    private String recipientContact;
    private Address recipientAddress;
    private String memo;
}
