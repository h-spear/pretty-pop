package prettypop.shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.ItemStatus;
import prettypop.shop.entity.Review;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired private ItemRepository itemRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private EntityManager em;

    @Test
    void findTopRatingByCategoryTest_카테고리별로평점높은상품가져오기() throws Exception {
        // given
        Map<Integer, Long> itemIds = initData();

        // when
        List<Item> topItems = itemRepository.findTopRatingByCategory(2);

        // then
        assertThat(topItems.size()).isEqualTo(6);

        List<Long> topItemIds = topItems.stream().map(item -> item.getId()).collect(Collectors.toList());
        assertThat(topItemIds).contains(
                itemIds.get(0), itemIds.get(1),
                itemIds.get(5), itemIds.get(3),
                itemIds.get(6), itemIds.get(7)
        );
    }

    @Test
    void findTopRatingByCategoryTest_같은카테고리에서는평점내림차순() throws Exception {
        // given
        // item1 평점 5.0 (리뷰 개수 1)
        // item2 평점 4.5 (리뷰 개수 2)
        // item3 평점 5.0 (리뷰 개수 2)
        Item item1 = itemRepository.save(Item.builder().name("banner-item1").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build());
        Item item2 = itemRepository.save(Item.builder().name("banner-item2").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build());
        Item item3 = itemRepository.save(Item.builder().name("banner-item3").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build());
        Review review1 = reviewRepository.save(new Review(item1, 5, "content...", null));
        Review review2 = reviewRepository.save(new Review(item2, 4, "content...", null));
        Review review3 = reviewRepository.save(new Review(item2, 4, "content...", null));
        Review review4 = reviewRepository.save(new Review(item3, 5, "content...", null));
        Review review5 = reviewRepository.save(new Review(item3, 5, "content...", null));

        Long item1Id = item1.getId();
        Long item2Id = item2.getId();
        Long item3Id = item3.getId();

        em.flush();
        em.clear();

        // when
        List<Item> topItems = itemRepository.findTopRatingByCategory(2);

        // then
        assertThat(topItems.size()).isEqualTo(2);
        assertThat(topItems.get(0).getId()).isEqualTo(item3Id);   // 평점 5.0 (1등)
        assertThat(topItems.get(0).getName()).isEqualTo("banner-item3");
        assertThat(topItems.get(1).getId()).isEqualTo(item1Id);   // 평점 5.0 (2등)  평점이 같다면 리뷰 개수 내림차순
        assertThat(topItems.get(1).getName()).isEqualTo("banner-item1");
    }

    private Map<Integer, Long> initData() {
        Map<Integer, Long> itemIds = new HashMap<>();
        List<Item> items = new ArrayList<>();
        items.add(itemRepository.save(Item.builder().name("banner-item1").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build()));
        items.add(itemRepository.save(Item.builder().name("banner-item2").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build()));
        items.add(itemRepository.save(Item.builder().name("banner-item3").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BANNER).build()));
        items.add(itemRepository.save(Item.builder().name("artsign-item1").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.ART_SIGN).build()));
        items.add(itemRepository.save(Item.builder().name("artsign-item2").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.ART_SIGN).build()));
        items.add(itemRepository.save(Item.builder().name("artsign-item3").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.ART_SIGN).build()));
        items.add(itemRepository.save(Item.builder().name("badge-item1").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BADGE_MEDAL).build()));
        items.add(itemRepository.save(Item.builder().name("badge-item2").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BADGE_MEDAL).build()));
        items.add(itemRepository.save(Item.builder().name("badge-item3").stockQuantity(9999).itemStatus(ItemStatus.NEW).category(Category.BADGE_MEDAL).build()));
        reviewRepository.save(new Review(items.get(0), 5, "content...", null));
        reviewRepository.save(new Review(items.get(1), 4, "content...", null));
        reviewRepository.save(new Review(items.get(2), 3, "content...", null));
        reviewRepository.save(new Review(items.get(3), 2, "content...", null));
        reviewRepository.save(new Review(items.get(4), 1, "content...", null));
        reviewRepository.save(new Review(items.get(5), 5, "content...", null));
        reviewRepository.save(new Review(items.get(6), 4, "content...", null));
        reviewRepository.save(new Review(items.get(7), 3, "content...", null));
        reviewRepository.save(new Review(items.get(8), 2, "content...", null));
        em.flush();
        em.clear();
        for (int i = 0; i < 9; ++i) {
            itemIds.put(i, items.get(i).getId());
        }
        return itemIds;
    }
}