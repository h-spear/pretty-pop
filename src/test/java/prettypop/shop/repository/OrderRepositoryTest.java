package prettypop.shop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired private MemberRepository memberRepository;
    @Autowired private OrderRepository orderRepository;

    @Test
    void findAllByMemberAndOrderDateTest() throws Exception {
        // given
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
        Long memberId = savedMember.getId();

        Delivery dummyDelivery = new Delivery("test", "010-1234-5678", new Address("12345", "address1", "address2", null), null);

        Order order1 = Order.builder().orderItems(new ArrayList<>()).delivery(dummyDelivery).member(savedMember).build();
        ReflectionTestUtils.setField(order1, "orderDate", LocalDateTime.of(2023, 3, 10, 10, 20));
        Order savedOrder1 = orderRepository.save(order1);

        Order order2 = Order.builder().orderItems(new ArrayList<>()).delivery(dummyDelivery).member(savedMember).build();
        ReflectionTestUtils.setField(order2, "orderDate", LocalDateTime.of(2023, 4, 10, 10, 20));
        Order savedOrder2 = orderRepository.save(order2);

        Order order3 = Order.builder().orderItems(new ArrayList<>()).delivery(dummyDelivery).member(savedMember).build();
        ReflectionTestUtils.setField(order3, "orderDate", LocalDateTime.of(2023, 4, 20, 10, 20));
        Order savedOrder3 = orderRepository.save(order3);

        // when
        List<Order> result1 = orderRepository.findAllByMemberAndOrderDate(memberId, 2023, 3);
        List<Order> result2 = orderRepository.findAllByMemberAndOrderDate(memberId, 2023, 4);

        System.out.println(result1);
        System.out.println(result2);
        // then
        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1).containsExactly(savedOrder1);
        assertThat(result2.size()).isEqualTo(2);
        assertThat(result2).containsExactly(savedOrder3, savedOrder2);  // orderDate 기준 내림차순 정렬
    }
}