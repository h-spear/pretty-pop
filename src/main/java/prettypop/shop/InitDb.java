package prettypop.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.entity.*;
import prettypop.shop.repository.ItemRepository;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.ReviewRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final ItemRepository itemRepository;
        private final ReviewRepository reviewRepository;
        private final PasswordEncoder passwordEncoder;

        public void dbInit() {
            int memberCount = 10;
            int itemCount = 20;
            int reviewCountPerItem = 0;

            List<Member> members = new ArrayList<>();
            Random random = new Random();

            for (int i = 1; i <= memberCount; ++i) {
                Member member = memberRepository.save(
                        Member.builder()
                                .username("member" + i)
                                .name("name" + i)
                                .nickname("nick" + i)
                                .point(1000000)
                                .password(passwordEncoder.encode("password"))
                                .address(new Address("1234", "도로", "지번", "디테일"))
                                .phoneNumber("010-1234-5677")
                                .build()
                );
                members.add(member);
            }

            for (int i = 1; i <= itemCount; ++i) {
                Item savedItem = itemRepository.save(
                        Item.builder()
                                .name("itemName" + i)
                                .description("description..." + i)
                                .stockQuantity(9999)
                                .originalPrice(1000)
                                .purchasePrice(900)
                                .earnedPoint(50)
                                .category(Category.category1)
                                .thumbnailImageUrl("https://dummyimage.com/300x300/d4d4d4/454545.jpg&text=Product" + i)
                                .itemStatus(ItemStatus.NEW)
                                .build()
                );
                int reviewCount = random.nextInt(reviewCountPerItem + 1);

                // review 등록
                for (int r = 1; r <= reviewCount; ++r) {
                    reviewRepository.save(
                            new Review(savedItem,
                                    random.nextInt(6),
                                    "review..." + r,
                                    members.get(random.nextInt(members.size())))
                    );
                }
            }
        }
    }
}
