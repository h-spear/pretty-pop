package prettypop.shop.repository.custom;

import prettypop.shop.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {

    List<Item> findTopRatingByCategory(int top);
}
