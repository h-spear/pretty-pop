package prettypop.shop.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.dto.item.ItemCountRequest;
import prettypop.shop.dto.order.OrderItemDto;
import prettypop.shop.entity.CartItem;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.WishItem;
import prettypop.shop.repository.CartItemRepository;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.WishItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ItemServiceTest {

    @Mock ItemRepository itemRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock WishItemRepository wishItemRepository;
    @Mock MemberRepository memberRepository;
    @InjectMocks ItemService itemService;

    @Test
    @DisplayName("상품 번호와 개수로 OrderItem 리스트를 만든다")
    void getOrderItemsTest() throws Exception {
        // given
        List<ItemCountRequest> requests = new ArrayList<>();
        requests.add(new ItemCountRequest(1L, 5));
        requests.add(new ItemCountRequest(2L, 4));

        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 2; ++i) {
            Item item = Item.builder().name("item" + i).build();
            ReflectionTestUtils.setField(item, "id", (long) i);
            items.add(item);
        }

        List<Long> itemIds = new ArrayList<>(Stream.of(1L, 2L)
                .collect(Collectors.toList()));

        // mock
        when(itemRepository.findAllById(itemIds)).thenReturn(items);

        // when
        List<OrderItemDto> orderItems = itemService.getOrderItems(requests);

        // then
        verify(itemRepository, times(1)).findAllById(itemIds);
        assertThat(orderItems.size()).isEqualTo(2);
        assertThat(orderItems.get(0).getItemId()).isEqualTo(1L);
        assertThat(orderItems.get(0).getItemName()).isEqualTo("item1");
        assertThat(orderItems.get(0).getQuantity()).isEqualTo(5);
        assertThat(orderItems.get(1).getItemId()).isEqualTo(2L);
        assertThat(orderItems.get(1).getItemName()).isEqualTo("item2");
        assertThat(orderItems.get(1).getQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("장바구니에 상품이 없으면 상품을 추가한다")
    void addCartItemTest() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        int count = 5;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);

        Long fakeCartItemId = 3L;
        CartItem cartItem = new CartItem(member, item, count);
        ReflectionTestUtils.setField(cartItem, "id", fakeCartItemId);

        // mock
        when(cartItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.empty());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // when
        itemService.addCartItem(memberId, itemId, count);

        // then
        verify(cartItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        assertThat(cartItem.getMember()).isEqualTo(member);
        assertThat(cartItem.getMember().getShoppingCart()).containsExactly(cartItem);
        assertThat(cartItem.getItem()).isEqualTo(item);
        assertThat(cartItem.getCount()).isEqualTo(count);
    }

    @Test
    @DisplayName("장바구니에 이미 존재하는 상품을 추가하면 개수만 증가한다.")
    void addCartItemTest2() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long cartItemId = 3L;
        int originalCount = 3;
        int addCount = 5;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);
        CartItem cartItem = new CartItem(member, item, originalCount);  // 기존에 3개가 장바구니에 존재
        ReflectionTestUtils.setField(cartItem, "id", cartItemId);
        member.addCartItem(cartItem);

        // mock
        when(cartItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.of(cartItem));

        // when
        assertThat(cartItem.getCount()).isEqualTo(originalCount);
        itemService.addCartItem(memberId, itemId, addCount);

        // then
        verify(cartItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        assertThat(cartItem.getCount()).isEqualTo(originalCount + addCount);
    }

    @Test
    @DisplayName("장바구니에 있는 상품을 삭제한다")
    void deleteCartItemTest() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long cartItemId = 3L;
        int count = 5;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);
        CartItem cartItem = new CartItem(member, item, count);  // 기존에 3개가 장바구니에 존재
        ReflectionTestUtils.setField(cartItem, "id", cartItemId);

        // mock
        when(cartItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.of(cartItem));

        // when
        itemService.deleteCartItem(memberId, itemId);

        // then
        verify(cartItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("장바구니에 있는 상품의 개수를 변경한다")
    void updateCartItemQuantityTest() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long cartItemId = 3L;
        int originalCount = 3;
        int updateCount = 5;    // 3개 -> 5개로 변경

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);
        CartItem cartItem = new CartItem(member, item, originalCount);
        ReflectionTestUtils.setField(cartItem, "id", cartItemId);

        // mock
        when(cartItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.of(cartItem));

        // when
        itemService.updateCartItemQuantity(memberId, itemId, updateCount);

        // then
        verify(cartItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        assertThat(cartItem.getCount()).isEqualTo(updateCount);
    }

    @Test
    @DisplayName("찜 목록에 상품을 추가한다.")
    void addWishItemList() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long fakeWishItemId = 3L;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);

        WishItem wishItem = new WishItem(member, item);
        ReflectionTestUtils.setField(wishItem, "id", fakeWishItemId);

        // mock
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(wishItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.empty());
        when(wishItemRepository.save(any(WishItem.class))).thenReturn(wishItem);

        // when
        itemService.addWish(memberId, itemId);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(wishItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        verify(wishItemRepository, times(1)).save(any(WishItem.class));
        assertThat(member.getWishList().size()).isEqualTo(1);
        assertThat(member.getWishList().get(0)).isEqualTo(wishItem);
    }

    @Test
    @DisplayName("이미 찜 목록에 있는 상품을 또 추가해도 아무 일이 발생하지 않는다")
    void addWishItemList_DuplicateWishItem() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long fakeWishItemId = 3L;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);
        WishItem wishItem = new WishItem(member, item);
        ReflectionTestUtils.setField(wishItem, "id", fakeWishItemId);
        member.addWishItem(wishItem);

        // mock
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(wishItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.of(wishItem));

        // when
        itemService.addWish(memberId, itemId);

        // then
        verify(memberRepository, times(1)).findById(memberId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(wishItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
    }

    @Test
    @DisplayName("찜 목록에 있는 상품을 삭제한다")
    void deleteWishTest() throws Exception {
        // given
        Long memberId = 1L;
        Long itemId = 2L;
        Long wishItemId = 3L;

        Member member = Member.builder().username("member").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        Item item = Item.builder().name("item").build();
        ReflectionTestUtils.setField(item, "id", itemId);
        WishItem wishItem = new WishItem(member, item);  // 기존에 3개가 장바구니에 존재
        ReflectionTestUtils.setField(wishItem, "id", wishItemId);

        // mock
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(wishItemRepository.findByMemberAndItem(memberId, itemId)).thenReturn(Optional.of(wishItem));

        // when
        itemService.deleteWish(memberId, itemId);

        // then
        verify(wishItemRepository, times(1)).findByMemberAndItem(memberId, itemId);
        verify(wishItemRepository, times(1)).delete(wishItem);
    }
}