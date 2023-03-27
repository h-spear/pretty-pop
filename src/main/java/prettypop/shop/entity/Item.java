package prettypop.shop.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Item extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
    private String description;

    private int discountRate;
    private int earnedPointRate;

    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    private String Category;

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new RuntimeException("상품의 재고량이 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }
}
