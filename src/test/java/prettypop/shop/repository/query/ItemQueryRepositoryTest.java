package prettypop.shop.repository.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import prettypop.shop.dto.item.ItemQueryCondition;
import prettypop.shop.dto.item.ItemQueryDto;
import prettypop.shop.dto.item.ItemQueryOrder;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.ItemStatus;
import prettypop.shop.entity.Review;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.ReviewRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ItemQueryRepositoryTest {

    @Autowired private ItemRepository itemRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private TestEntityManager testEntityManager;

    private ItemQueryRepository itemQueryRepository;
    private EntityManager em;

    private Map<Integer, Long> itemIdMap;

    @BeforeEach
    void beforeEach() {
        em = testEntityManager.getEntityManager();
        itemQueryRepository = new ItemQueryRepository(em);
        itemIdMap = initData();
    }

    @Test
    void queryTest1_검색조건_페이징() throws Exception {
        // given
        ItemQueryCondition condition = new ItemQueryCondition();
        condition.setKeyword("item");
        condition.setPriceGe(20000);
        condition.setPriceLe(40000);
        condition.setRatingGe(1);

        Pageable pageable;

        // when
        pageable = PageRequest.of(0, 2);
        Page<ItemQueryDto> result1 = itemQueryRepository.query(condition, pageable);

        pageable = PageRequest.of(1, 2);
        Page<ItemQueryDto> result2 = itemQueryRepository.query(condition, pageable);

        // then
        assertThat(result1.getNumber()).isEqualTo(0);
        assertThat(result1.getSize()).isEqualTo(2);
        assertThat(result1.getTotalPages()).isEqualTo(2);
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(result1.isFirst()).isTrue();
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.isLast()).isFalse();

        assertThat(result2.getNumber()).isEqualTo(1);
        assertThat(result2.getSize()).isEqualTo(2);
        assertThat(result2.getTotalPages()).isEqualTo(2);
        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(result2.isFirst()).isFalse();
        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.isLast()).isTrue();
    }

    @Test
    void queryTest2_검색조건_카테고리() throws Exception {
        // given
        ItemQueryCondition condition = new ItemQueryCondition();
        condition.setKeyword("item");
        condition.setCategory(Category.BANNER);

        Pageable pageable;

        // when
        pageable = PageRequest.of(0, 3);
        Page<ItemQueryDto> result1 = itemQueryRepository.query(condition, pageable);

        // then
        assertThat(result1.getNumber()).isEqualTo(0);
        assertThat(result1.getSize()).isEqualTo(3);
        assertThat(result1.getTotalPages()).isEqualTo(1);
        assertThat(result1.getNumberOfElements()).isEqualTo(3);
        assertThat(result1.isFirst()).isTrue();
        assertThat(result1.hasNext()).isFalse();
        assertThat(result1.isLast()).isTrue();

        List<Long> itemIds = result1.getContent().stream().map(dto -> dto.getId()).collect(Collectors.toList());
        assertThat(itemIds).contains(itemIdMap.get(0), itemIdMap.get(1), itemIdMap.get(2));
    }

    @Test
    void queryTest3_검색조건_정렬조건() throws Exception {
        // given
        ItemQueryCondition condition = new ItemQueryCondition();
        condition.setKeyword("item");
        condition.setOrder(ItemQueryOrder.PRICE_DESC);

        Pageable pageable;

        // when
        pageable = PageRequest.of(0, 3);
        Page<ItemQueryDto> result1 = itemQueryRepository.query(condition, pageable);

        pageable = PageRequest.of(1, 3);
        Page<ItemQueryDto> result2 = itemQueryRepository.query(condition, pageable);

        // then
        assertThat(result1.getNumber()).isEqualTo(0);
        assertThat(result1.getSize()).isEqualTo(3);
        assertThat(result1.getTotalPages()).isEqualTo(2);
        assertThat(result1.getNumberOfElements()).isEqualTo(3);
        assertThat(result1.isFirst()).isTrue();
        assertThat(result1.hasNext()).isTrue();
        assertThat(result1.isLast()).isFalse();

        List<Long> itemIds1 = result1.getContent().stream().map(dto -> dto.getId()).collect(Collectors.toList());
        assertThat(itemIds1).containsExactly(itemIdMap.get(5), itemIdMap.get(4), itemIdMap.get(3));

        assertThat(result2.getNumber()).isEqualTo(1);
        assertThat(result2.getSize()).isEqualTo(3);
        assertThat(result2.getTotalPages()).isEqualTo(2);
        assertThat(result2.getNumberOfElements()).isEqualTo(3);
        assertThat(result2.isFirst()).isFalse();
        assertThat(result2.hasNext()).isFalse();
        assertThat(result2.isLast()).isTrue();

        List<Long> itemIds2 = result2.getContent().stream().map(dto -> dto.getId()).collect(Collectors.toList());
        assertThat(itemIds2).containsExactly(itemIdMap.get(2), itemIdMap.get(1), itemIdMap.get(0));
    }

    @Test
    void findTopRating() throws Exception {
        // when
        List<ItemQueryDto> topItems = itemQueryRepository.findTopRating(5);

        // then
        assertThat(topItems.size()).isEqualTo(5);

        // 모든 평점의 합계 순으로 내림차순
        // 평점의 합이 같다면 평점의 평균으로 내림차순
        // 평균 평점까지 같다면 ID 오름차순
        List<Long> itemIds = topItems.stream().map(dto -> dto.getId()).collect(Collectors.toList());
        assertThat(itemIds).containsExactly(
                itemIdMap.get(0), // 평점 합 9
                itemIdMap.get(3), // 평점 합 5 (평균 평점 5)
                itemIdMap.get(1), // 평점 합 5 (평균 평점 2.5)
                itemIdMap.get(5), // 평점 합 5 (평균 평점 2.5)
                itemIdMap.get(4)  // 평점 합 4
        );
    }


    /**
     * item1 평점 : 4.5, 리뷰 수 : 2, 가격 : 10000, 카테고리 : 배너
     * item2 평점 : 2.5, 리뷰 수 : 2, 가격 : 20000, 카테고리 : 배너
     * item3 평점 : 1.0, 리뷰 수 : 1, 가격 : 30000, 카테고리 : 배너
     * item4 평점 : 5.0, 리뷰 수 : 1, 가격 : 40000, 카테고리 : 아트사인
     * item5 평점 : 4.0, 리뷰 수 : 1, 가격 : 50000, 카테고리 : 아트사인
     * item6 평점 : 2.5, 리뷰 수 : 2, 가격 : 60000, 카테고리 : 아트사인
     */
    private Map<Integer, Long> initData() {
        Map<Integer, Long> itemIdMap = new HashMap<>();
        List<Item> items = new ArrayList<>();
        String[] names = {"banner-item1", "banner-item2", "banner-item3", "artsign-item1", "artsign-item2", "artsign-item3"};
        Category[] categories = {Category.BANNER, Category.BANNER, Category.BANNER, Category.ART_SIGN, Category.ART_SIGN, Category.ART_SIGN};
        for (int i = 0; i < 6; ++i) {
            Item item = Item.builder().name(names[i]).itemStatus(ItemStatus.NEW).category(categories[i]).originalPrice(90000).purchasePrice((i + 1) * 10000)
                    .thumbnailImageUrl(null).salesVolume(0).earnedPoint(50).build();
            items.add(itemRepository.save(item));
        }
        reviewRepository.save(new Review(items.get(0), 5, "content...", null));
        reviewRepository.save(new Review(items.get(0), 4, "content...", null));
        reviewRepository.save(new Review(items.get(1), 3, "content...", null));
        reviewRepository.save(new Review(items.get(1), 2, "content...", null));
        reviewRepository.save(new Review(items.get(2), 1, "content...", null));
        reviewRepository.save(new Review(items.get(3), 5, "content...", null));
        reviewRepository.save(new Review(items.get(4), 4, "content...", null));
        reviewRepository.save(new Review(items.get(5), 3, "content...", null));
        reviewRepository.save(new Review(items.get(5), 2, "content...", null));
        em.flush();
        em.clear();
        for (int i = 0; i < 6; ++i) {
            itemIdMap.put(i, items.get(i).getId());
        }
        return itemIdMap;
    }
}