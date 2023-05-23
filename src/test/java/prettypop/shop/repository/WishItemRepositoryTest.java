package prettypop.shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.entity.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WishItemRepositoryTest {

    @Autowired WishItemRepository wishItemRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;

    @Test
    void findByMemberAndItemTest() throws Exception {
        // given
        Member member = memberRepository.save(getMember());
        Item item = itemRepository.save(Item.builder().name("item1").build());
        WishItem wishItem = wishItemRepository.save(new WishItem(member, item));
        Long memberId = member.getId();
        Long itemId = item.getId();

        // when
        WishItem findWishItem = wishItemRepository.findByMemberAndItem(memberId, itemId).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(findWishItem).isEqualTo(wishItem);
        assertThat(findWishItem.getMember()).isEqualTo(member);
        assertThat(findWishItem.getItem()).isEqualTo(item);
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