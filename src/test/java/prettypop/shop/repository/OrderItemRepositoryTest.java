package prettypop.shop.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.entity.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired OrderItemRepository orderItemRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    private static Map<Integer, Long> itemIdMap = new HashMap<>();
    private static Map<Integer, Long> memberIdMap = new HashMap<>();
    private static Map<Integer, Long> orderIdMap = new HashMap<>();
    private static Map<Integer, Long> orderItemIdMap = new HashMap<>();

    @BeforeEach
    void beforeEach() {
        initData();
    }
    
    @AfterEach
    void afterEach() {
        itemIdMap.clear();
        memberIdMap.clear();
        orderIdMap.clear();
        orderItemIdMap.clear();
    }

    @Test
    void findNotReviewedByMemberAndItemTest() throws Exception {
        // given
        Long memberId = memberIdMap.get(0);
        Long itemId = itemIdMap.get(0);

        // when
        List<OrderItem> orderItems = orderItemRepository.findNotReviewedByMemberAndItem(memberId, itemId);

        // then
        assertThat(orderItems.size()).isEqualTo(2);
        assertThat(orderItems.get(0).getId()).isEqualTo(orderItemIdMap.get(0));
        assertThat(orderItems.get(0).getItem().getId()).isEqualTo(itemId);
        assertThat(orderItems.get(1).getId()).isEqualTo(orderItemIdMap.get(5));
        assertThat(orderItems.get(1).getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void updateBulkHasReviewTrueTest() throws Exception {
        // 모든 주문 아이템의 리뷰 작성 상태는 false
        List<OrderItem> orderItems = orderItemRepository.findAll();

        // before
        orderItems.forEach(oi -> assertThat(oi.isHasReview()).isFalse());

        // when
        orderItemRepository.updateBulkHasReviewTrue(orderItems);

        em.flush(); // 벌크 연산이기 때문에 영속성 컨텍스트를 초기화
        em.clear();

        // then
        List<OrderItem> afterOrderItems = orderItemRepository.findAll();
        afterOrderItems.forEach(oi -> assertThat(oi.isHasReview()).isTrue());
    }

    @Test
    void findNotReviewedItemTest() throws Exception {
        // given
        Long memberId = memberIdMap.get(0);

        // when
        List<Item> notReviewedItems = orderItemRepository.findNotReviewedItem(memberId);

        // then
        assertThat(notReviewedItems.size()).isEqualTo(5);
        assertThat(notReviewedItems.stream().map(i -> i.getId()).collect(Collectors.toList()))
                .contains(itemIdMap.get(0), itemIdMap.get(1), itemIdMap.get(2), itemIdMap.get(3), itemIdMap.get(4));
    }

    /**
     * Item 5개 등록
     * Item 5개로 똑같은 Order 2개 생성
     */
    private void initData() {
        Member member = Member.builder()
                .username("test")
                .password("password")
                .name("testUser")
                .birthDate(LocalDate.of(2000,12,14))
                .gender(Gender.MALE)
                .nickname("nickname")
                .phoneNumber("010-1234-5678")
                .address(new Address("12345", "address1", "address2", null))
                .build();
        Member savedMember = memberRepository.save(member);
        memberIdMap.put(0, savedMember.getId());

        // 상품 5개 등록
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            Item item = Item.builder()
                    .name("item...")
                    .itemStatus(ItemStatus.NEW)
                    .category(Category.BANNER)
                    .originalPrice(10000)
                    .purchasePrice(5000)
                    .stockQuantity(10)
                    .build();
            Item savedItem = itemRepository.save(item);
            items.add(savedItem);
            itemIdMap.put(i, savedItem.getId());
        }

        // 같은 상품으로 주문 2번
        for (int i = 0; i < 2; ++i) {
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (int j = 0; j < 5; ++j) {
                OrderItem orderItem = OrderItem.createOrderItem(items.get(j), 3);
                OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                orderItems.add(savedOrderItem);
                orderItemIdMap.put(i * 5 + j, savedOrderItem.getId());
            }
            Delivery delivery = new Delivery("test", "010-1234-5678", new Address("12345", "address1", "address2", null), null);

            Order order = Order.builder()
                    .member(savedMember)
                    .orderItems(orderItems)
                    .delivery(delivery)
                    .build();
            Order savedOrder = orderRepository.save(order);
            orderIdMap.put(i, savedOrder.getId());
        }
    }
}