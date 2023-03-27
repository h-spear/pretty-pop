package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class WishItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "WISH_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    protected WishItem() {
    }

    public WishItem(Member member, Item item) {
        this.member = member;
        this.item = item;
    }
}
