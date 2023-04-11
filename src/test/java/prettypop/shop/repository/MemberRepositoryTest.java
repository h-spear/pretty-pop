package prettypop.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import prettypop.shop.dto.member.MemberDto;
import prettypop.shop.entity.*;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findByUsernameWithRolesTest() throws Exception {
        // given
        Member member = Member.builder()
                .username("username")
                .password("password")
                .build();
        member.addRole("ROLE_ADMIN");
        Member savedMember = memberRepository.save(member);
        Long savedId = savedMember.getId();

        // when
        Member findMember = memberRepository.findByUsername("username").get();

        // then
        assertThat(findMember.getId()).isEqualTo(savedId);
        assertThat(findMember.getUsername()).isEqualTo("username");
        assertThat(findMember.getPassword()).isEqualTo("password");
        assertThat(findMember.getRoles().size()).isEqualTo(2);
        assertThat(findMember.getRoles()).containsExactly("ROLE_USER", "ROLE_ADMIN");
    }
    
    @Test
    void findBasicInfoByIdTest() throws Exception {
        // given
        Long memberId = generateData(5, 10);

        // when
        MemberDto memberDto = memberRepository.findBasicInfoById(memberId).orElseThrow(IllegalArgumentException::new);

        // then
        assertThat(memberDto.getUsername()).isEqualTo("test1");
        assertThat(memberDto.getName()).isEqualTo("김래빗");
        assertThat(memberDto.getGender()).isEqualTo(Gender.MALE);
        assertThat(memberDto.getBirthDate()).isEqualTo(LocalDate.of(2000, 01, 01));
        assertThat(memberDto.getAddress()).isEqualTo(new Address("12345", "도로명 주소", "지번 주소", "상세 주소"));
        assertThat(memberDto.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(memberDto.getEmail()).isEqualTo("test1@prettypop.com");
        assertThat(memberDto.getPoint()).isEqualTo(3000);
        assertThat(memberDto.getOrderCount()).isEqualTo(5);
        assertThat(memberDto.getReviewCount()).isEqualTo(10);
    }
    

    private Long generateData(int orderCount, int reviewCount) {
        Member member = Member.builder()
                .username("test1")
                .password("password")
                .name("김래빗")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2000, 01, 01))
                .nickname("rabbit")
                .address(new Address("12345", "도로명 주소", "지번 주소", "상세 주소"))
                .phoneNumber("010-1234-5678")
                .email("test1@prettypop.com")
                .point(3000)
                .build();
        Member savedMember = memberRepository.save(member);

        for (int i = 0; i < orderCount; ++i) {
            // Delivery - save는 하지 않음.
            Delivery delivery = new Delivery(member.getName(), member.getPhoneNumber(), member.getAddress(), "memo..." + i);
            Order order = Order.builder()
                    .member(member)
                    .orderItems(new ArrayList<>())
                    .delivery(delivery)
                    .build();
            orderRepository.save(order);
        }

        for (int i = 0; i < reviewCount; ++i) {
            Review review = new Review(null, 5, "test review..." + i, savedMember);
            reviewRepository.save(review);
        }

        return savedMember.getId();
    }
}