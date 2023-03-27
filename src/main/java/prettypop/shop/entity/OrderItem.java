package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private int originalPrice;
    private int discountPrice;
    private int earnedPoint;
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        item.removeStock(count);
        orderItem.item = item;
        orderItem.originalPrice = item.getPrice();
        orderItem.discountPrice = (item.getPrice() * item.getDiscountRate()) / 100;
        orderItem.earnedPoint = (item.getPrice() * item.getEarnedPointRate()) / 100;
        orderItem.count = count;
        return orderItem;
    }

    public void cancel() {
        this.item.addStock(this.count);
    }

    public int getTotalPrice() {
        return (this.originalPrice - this.discountPrice) * this.count;
    }
}
