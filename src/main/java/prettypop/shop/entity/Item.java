package prettypop.shop.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Item extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private String description;

    private int originalPrice;
    private int purchasePrice;
    private int earnedPoint;

    private int stockQuantity;
    private int salesVolume;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String thumbnailImageUrl;

    @OneToMany(mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @ElementCollection
    private List<String> imageFileUrls;

    protected Item() {
    }

    @Builder
    public Item(String name, String description, int originalPrice, int purchasePrice, int earnedPoint, int stockQuantity, int salesVolume, ItemStatus itemStatus, Category category, String thumbnailImageUrl, List<String> imageFileUrls) {
        if (this.purchasePrice > this.originalPrice) {
            throw new IllegalArgumentException("구매 가격은 원래 가격보다 작거나 같아야 합니다.");
        }
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.purchasePrice = purchasePrice;
        this.earnedPoint = earnedPoint;
        this.stockQuantity = stockQuantity;
        this.salesVolume = salesVolume;
        this.itemStatus = itemStatus;
        this.category = category;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.imageFileUrls = imageFileUrls;
    }

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
