package prettypop.shop.entity;

import lombok.Builder;
import lombok.Getter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @NotNull
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private int earnedPoint;
    private int usedPoint;
    private int deliveryFee;
    private int paymentAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private boolean guest;
    private String guestPassword;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID")
    private Delivery delivery;

    protected Order() {
    }

    @Builder
    public Order(Member member, List<OrderItem> orderItems, int earnedPoint, int usedPoint, int deliveryFee, int paymentAmount, Delivery delivery) {
        setMember(member);
        orderItems.forEach(orderItem -> orderItem.setOrder(this));
        this.orderDate = LocalDateTime.now();
        this.orderItems = orderItems;
        this.earnedPoint = earnedPoint;
        this.usedPoint = usedPoint;
        this.deliveryFee = deliveryFee;
        this.paymentAmount = paymentAmount;
        this.orderStatus = OrderStatus.ORDER;
        setDelivery(delivery);
        this.guest = false;
    }

    @Builder(builderClassName = "guestBuilder", builderMethodName = "guestBuilder")
    public Order(List<OrderItem> orderItems, int deliveryFee, int paymentAmount, Delivery delivery, String guestPassword) {
        orderItems.forEach(orderItem -> orderItem.setOrder(this));
        this.member = null;
        this.orderDate = LocalDateTime.now();
        this.orderItems = orderItems;
        this.deliveryFee = deliveryFee;
        this.paymentAmount = paymentAmount;
        this.orderStatus = OrderStatus.ORDER;
        setDelivery(delivery);
        this.guest = true;
        this.guestPassword = guestPassword;
    }

    public void completeOrder() {
        orderStatus = OrderStatus.COMPLETED;
        delivery.setDeliveryStatus(DeliveryStatus.COMPLETED);
        if (member != null) {
            member.increasePoint(earnedPoint);
        }
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
        if (member != null) {
            this.member = member;
            member.getOrders().add(this);
        }
    }
}
