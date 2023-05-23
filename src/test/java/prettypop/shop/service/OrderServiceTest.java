package prettypop.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.order.OrderCreateParam;
import prettypop.shop.dto.order.OrderGuestCreateParam;
import prettypop.shop.entity.*;
import prettypop.shop.repository.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock DeliveryRepository deliveryRepository;
    @Mock OrderItemRepository orderItemRepository;
    @Mock ItemRepository itemRepository;
    @Mock OrderRepository orderRepository;
    @Mock CartItemRepository cartItemRepository;

    @InjectMocks
    OrderService orderService;

    private static final Long MEMBER_ID = 1L;
    private static final Long ORDER_ID = 20L;
    private static final String RECIPIENT_NAME = "test";
    private static final String RECIPIENT_CONTACT = "010-1234-5678";
    private static final String GUEST_PASSWORD = "1234";
    private static final Address RECIPIENT_ADDRESS = new Address("12345", "RoadAddress", "JibunAddress", "Detail");
    private static final String MEMO = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";
    private static final int EARNED_POINT = 1000;
    private static final int BASE_POINT = 1000000000;
    private static final int DELIVERY_FEE = 3000;
    private static final int PAYMENT_AMOUNT = 0;
    private static final int USED_POINT = 10000;

    static Member member;
    List<Item> items;
    List<CartItem> cartItems;
    Map<Item, Integer> itemCountMap;

    // mock
    Delivery delivery;
    List<OrderItem> orderItems;
    Order order;
    List<Long> cartItemIds;

    @BeforeEach
    void beforeEach() {
        initData();
        mockingData();
    }

    @Test
    @DisplayName("회원이 상품 주문에 성공한다")
    void createOrderTest() throws Exception {
        // given
        OrderCreateParam orderParam = getOrderCreateParam();

        // when
        orderService.createOrder(MEMBER_ID, orderParam);

        // then

        // 사용한 포인트만큼 회원 포인트를 차감시킨다.
        verify(memberRepository, times(1)).findById(MEMBER_ID);
        assertThat(member.getPoint()).isEqualTo(BASE_POINT - USED_POINT);

        // 배송 정보를 저장한다.
        verify(deliveryRepository, times(1)).save(any(Delivery.class));

        // OrderItem 저장한다.
        verify(orderItemRepository, times(1)).saveAll(any(Iterable.class));

        // 주문 정보를 저장한다.
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(3);
        assertThat(order.getOrderItems()).contains(orderItems.get(0), orderItems.get(1), orderItems.get(2));
        verify(orderRepository, times(1)).save(any(Order.class));

        // 장바구니에서 주문한 상품을 제거한다.
        verify(cartItemRepository, times(1)).findAllByMemberIdWithItem(MEMBER_ID);
        verify(cartItemRepository, times(1)).deleteBulkById(cartItemIds);
    }

    @Test
    @DisplayName("비회원으로 상품 주문에 성공한다")
    void createGuestOrderTest() throws Exception {
        // given
        OrderGuestCreateParam orderParam = getOrderGuestCreateParam();

        // when
        orderService.createGuestOrder(orderParam);

        // then
        // 배송 정보를 저장한다.
        verify(deliveryRepository, times(1)).save(any(Delivery.class));

        // OrderItem 저장한다.
        verify(orderItemRepository, times(1)).saveAll(any(Iterable.class));

        // 주문 정보를 저장한다.
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(3);
        assertThat(order.getOrderItems()).contains(orderItems.get(0), orderItems.get(1), orderItems.get(2));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("회원 주문 시 배송이 완료되면 회원에게 포인트가 지급되고 배송 완료, 주문 완료 상태가 된다.")
    void completeDeliveryTest() throws Exception {
        // mock
        when(orderRepository.findByIdWithFetchJoin(ORDER_ID)).thenReturn(Optional.of(order));

        // then
        orderService.completeDelivery(ORDER_ID);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(member.getPoint()).isEqualTo(BASE_POINT + EARNED_POINT);
        assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.COMPLETED);
    }

    private void mockingData() {
        delivery = new Delivery(RECIPIENT_NAME, RECIPIENT_CONTACT, RECIPIENT_ADDRESS, MEMO);
        orderItems = items.stream()
                .map(item -> OrderItem.createOrderItem(item, itemCountMap.get(item)))
                .collect(Collectors.toList());
        order = new Order(member, orderItems, EARNED_POINT, USED_POINT, DELIVERY_FEE, PAYMENT_AMOUNT, delivery);
        ReflectionTestUtils.setField(order, "id", ORDER_ID);
        cartItemIds = cartItems.stream().map(CartItem::getId).collect(Collectors.toList());

        when(memberRepository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(itemRepository.findAllById(any(Iterable.class))).thenReturn(items);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartItemRepository.findAllByMemberIdWithItem(MEMBER_ID)).thenReturn(cartItems);
    }

    private OrderCreateParam getOrderCreateParam() {
        OrderCreateParam orderParam = new OrderCreateParam();
        orderParam.setRecipientName(RECIPIENT_NAME);
        orderParam.setRecipientContact(RECIPIENT_CONTACT);
        orderParam.setRecipientAddress(RECIPIENT_ADDRESS);
        orderParam.setMemo(MEMO);
        orderParam.setEarnedPoint(EARNED_POINT);
        orderParam.setDeliveryFee(DELIVERY_FEE);
        orderParam.setPaymentAmount(PAYMENT_AMOUNT);
        orderParam.setUsedPoint(USED_POINT);

        List<ItemCountRequest> itemRequests = new ArrayList<>();
        itemRequests.add(new ItemCountRequest(items.get(0).getId(), 1));// 상품1을 1개 구매
        itemRequests.add(new ItemCountRequest(items.get(1).getId(), 2));// 상품2을 2개 구매
        itemRequests.add(new ItemCountRequest(items.get(2).getId(), 3));// 상품3을 3개 구매
        orderParam.setOrderItemRequests(itemRequests);
        return orderParam;
    }

    private OrderGuestCreateParam getOrderGuestCreateParam() {
        OrderGuestCreateParam orderParam = new OrderGuestCreateParam();
        orderParam.setRecipientName(RECIPIENT_NAME);
        orderParam.setRecipientContact(RECIPIENT_CONTACT);
        orderParam.setRecipientAddress(RECIPIENT_ADDRESS);
        orderParam.setMemo(MEMO);
        orderParam.setDeliveryFee(DELIVERY_FEE);
        orderParam.setPaymentAmount(PAYMENT_AMOUNT);
        orderParam.setGuestPassword(GUEST_PASSWORD);

        List<ItemCountRequest> itemRequests = new ArrayList<>();
        itemRequests.add(new ItemCountRequest(items.get(0).getId(), 1));// 상품1을 1개 구매
        itemRequests.add(new ItemCountRequest(items.get(1).getId(), 2));// 상품2을 2개 구매
        itemRequests.add(new ItemCountRequest(items.get(2).getId(), 3));// 상품3을 3개 구매
        orderParam.setOrderItemRequests(itemRequests);
        return orderParam;
    }

    void initData() {
        member = Member.builder().name("testUser")
                .point(BASE_POINT)
                .build();
        ReflectionTestUtils.setField(member, "id", MEMBER_ID);

        items = new ArrayList<>();
        itemCountMap = new HashMap<>();
        cartItems = new ArrayList<>();

        for (int i = 1; i <= 3; ++i) {
            Item item = Item.builder().name("item" + i)
                    .salesVolume(0).stockQuantity(9999)
                    .category(Category.BANNER).itemStatus(ItemStatus.NEW)
                    .build();
            ReflectionTestUtils.setField(item, "id", (long) i);
            items.add(item);
            itemCountMap.put(item, i);

            CartItem cartItem = new CartItem(member, item, i);
            ReflectionTestUtils.setField(cartItem, "id", (long) (i + 10));
            cartItems.add(cartItem);
        }
    }
}