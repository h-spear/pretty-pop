package prettypop.shop.dto.item;

import lombok.Data;
import prettypop.shop.entity.Category;

@Data
public class ItemQueryCondition {

    private String keyword;

    private Category category;
    private ItemQueryOrder order;

    private Integer ratingGe;
    private Integer reviewCountGe;
    private Integer priceGe;
    private Integer priceLe;

}
