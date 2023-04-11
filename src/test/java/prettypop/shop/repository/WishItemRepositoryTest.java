package prettypop.shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.WishItem;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WishItemRepositoryTest {

    @Autowired WishItemRepository wishItemRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ItemRepository itemRepository;

    @Test
    void findByMemberAndItemTest() throws Exception {
        // given
        Member member = memberRepository.save(Member.builder().username("test").build());
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
}