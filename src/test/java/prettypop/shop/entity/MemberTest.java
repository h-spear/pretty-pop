package prettypop.shop.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void memberBuilderTest() {
        Member member = Member.builder()
                .username("member1")
                .password("password")
                .nickname("rabbit")
                .name("김래빗")
                .point(3000)
                .address(new Address("12345", "도로명", "지번", "상세"))
                .email("rabbit@test.com")
                .phoneNumber("010-1234-5678")
                .build();

        System.out.println("member.username = " + member.getUsername());
        System.out.println("member.password = " + member.getPassword());
        System.out.println("member.nickname = " + member.getNickname());
        System.out.println("member.name = " + member.getName());
        System.out.println("member.point = " + member.getPoint());
        System.out.println("member.address = " + member.getAddress());
        System.out.println("member.email = " + member.getEmail());
        System.out.println("member.phoneNumber = " + member.getPhoneNumber());
    }
}