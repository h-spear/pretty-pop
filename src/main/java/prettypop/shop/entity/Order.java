package prettypop.shop.entity;

import lombok.Builder;
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

    protected Order() {
    }

    @Builder
    public Order(Member member, List<OrderItem> orderItems, int earnedPoint, int usedPoint, int deliveryFee, int paymentAmount, Delivery delivery) {
        setMember(member);
        orderItems.stream().forEach(orderItem -> orderItem.setOrder(this));
        this.orderDate = LocalDateTime.now();
        this.orderItems = orderItems;
        this.earnedPoint = earnedPoint;
        this.usedPoint = usedPoint;
        this.deliveryFee = deliveryFee;
        this.paymentAmount = paymentAmount;
        this.orderStatus = OrderStatus.ORDER;
        setDelivery(delivery);
    }

    public void completeOrder() {
        orderStatus = OrderStatus.COMPLETED;
        delivery.setDeliveryStatus(DeliveryStatus.COMPLETED);
        member.increasePoint(earnedPoint);
    }

    public void cancelOrder() {
        for (OrderItem orderItem: orderItems) {
            orderItem.cancel();
        }
        orderStatus = OrderStatus.CANCEL;
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
