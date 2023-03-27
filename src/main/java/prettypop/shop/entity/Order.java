package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "ORDERS")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private int earnedPoint;
    private int usedPoint;
    private int deliveryFee;
    private int paymentAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;


    public void createOrder(Member member, Delivery delivery, int usedPoint, int deliveryFee, OrderItem... orderItems) {
        setMember(member);
        setDelivery(delivery);
        this.deliveryFee = deliveryFee;
        this.usedPoint = usedPoint;
        this.orderDate = LocalDateTime.now();
        this.orderStatus = OrderStatus.ORDER;
        for (OrderItem orderItem: orderItems) {
            addOrderItem(orderItem);
            this.paymentAmount += orderItem.getTotalPrice();
        }
        this.paymentAmount += deliveryFee;
        this.paymentAmount -= usedPoint;
    }

    public void completeOrder() {
        orderStatus = OrderStatus.COMPLETED;
    }

    public void cancelOrder() {
        for (OrderItem orderItem: orderItems) {
            orderItem.cancel();
        }
        orderStatus = OrderStatus.CANCEL;
    }

    private void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        earnedPoint += orderItem.getEarnedPoint();
    }

    private void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    private void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
}
