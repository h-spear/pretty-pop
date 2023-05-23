package prettypop.shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.entity.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired CartItemRepository cartItemRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    @Test
    void findByMemberAndItemTest() throws Exception {
        // given
        Member member = memberRepository.save(getMember());
        Item item = itemRepository.save(Item.builder().name("item1").build());
        CartItem cartItem = cartItemRepository.save(new CartItem(member, item, 4));
        Long memberId = member.getId();
        Long itemId = item.getId();

        // when
        CartItem findCartItem = cartItemRepository.findByMemberAndItem(memberId, itemId).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findCartItem).isEqualTo(cartItem);
        assertThat(findCartItem.getMember()).isEqualTo(member);
        assertThat(findCartItem.getItem()).isEqualTo(item);
        assertThat(findCartItem.getCount()).isEqualTo(4);
    }
    
    @Test
    void findByMemberAndItemTest_존재하지않는정보() throws Exception {
        // given
        Member member = memberRepository.save(getMember());
        Item item = itemRepository.save(Item.builder().name("item1").build());
        CartItem cartItem = cartItemRepository.save(new CartItem(member, item, 4));
        Long memberId = member.getId();
        Long itemId = item.getId();

        // 잘못된 회원 ID
        Optional<CartItem> optional1 = cartItemRepository.findByMemberAndItem(9999L, itemId);
        assertThat(optional1.isEmpty()).isTrue();

        // 잘못된 상품 ID
        Optional<CartItem> optional2 = cartItemRepository.findByMemberAndItem(memberId, 9999L);
        assertThat(optional2.isEmpty()).isTrue();
    }

    @Test
    void findAllByMemberIdWithItemTest() throws Exception {
        // given
        Member member = memberRepository.save(getMember());
        Item item1 = itemRepository.save(Item.builder().name("item1").build());
        CartItem cartItem1 = cartItemRepository.save(new CartItem(member, item1, 4));
        Item item2 = itemRepository.save(Item.builder().name("item2").build());
        CartItem cartItem2 = cartItemRepository.save(new CartItem(member, item2, 3));

        // when
        List<CartItem> cartItems = cartItemRepository.findAllByMemberId(member.getId());

        // then
        assertThat(cartItems.size()).isEqualTo(2);
        assertThat(cartItems).contains(cartItem1, cartItem2);
        assertThat(cartItems.get(0).getItem()).isEqualTo(item1);    // fetch join
        assertThat(cartItems.get(1).getItem()).isEqualTo(item2);
    }


    @Test
    void deleteBulkByIdTest() throws Exception {
        // given
        Member member = memberRepository.save(getMember());
        CartItem cartItem1 = cartItemRepository.save(new CartItem(member, null, 4));
        CartItem cartItem2 = cartItemRepository.save(new CartItem(member, null, 3));
        CartItem cartItem3 = cartItemRepository.save(new CartItem(member, null, 5));
        CartItem cartItem4 = cartItemRepository.save(new CartItem(member, null, 6));
        em.flush();
        em.clear();

        List<Long> ids = new ArrayList<>();
        ids.add(cartItem1.getId());
        ids.add(cartItem2.getId());
        ids.add(cartItem3.getId());

        // when
        int remove = cartItemRepository.deleteBulkById(ids);

        // then
        assertThat(remove).isEqualTo(3);
    }

    private Member getMember() {
        return Member.builder()
                .username("test")
                .password("password")
                .name("testUser")
                .birthDate(LocalDate.of(2000,12,14))
                .gender(Gender.MALE)
                .nickname("nickname")
                .phoneNumber("010-1234-5678")
                .address(new Address("12345", "address1", "address2", null))
                .build();
    }
}