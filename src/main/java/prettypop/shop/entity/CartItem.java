package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "CART_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private int count;

    protected CartItem() {
    }

    public CartItem(Member member, Item item, int count) {
        this.member = member;
        this.item = item;
        this.count = count;
    }
}
