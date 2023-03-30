package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Review extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "REVIEW_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    private int rating;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member reviewer;

    protected Review() {
    }

    public Review(Item item, int rating, String content, Member reviewer) {
        this.item = item;
        this.rating = rating;
        this.content = content;
        this.reviewer = reviewer;
    }
}
