package prettypop.shop.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Profile("local")
//@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

//    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberRepository memberRepository;
        private final PasswordEncoder passwordEncoder;

        public void dbInit() {
            int memberCount = 30;

            List<Member> members = new ArrayList<>();

            String[] nick = new String[] {"Rabbit", "Giraffe", "Tiger", "Horse", "Monkey", "Lion", "Deer", "Fox", "Hyena", "Hedgehog",
                    "Pig", "Elephant", "Mole", "Raccoon", "Bear", "Panda", "Whale", "Sloth", "Duck", "Swan",
                    "Starling", "Frog", "Bee", "Butterfly", "Spider", "Goldbug", "Turtle", "Snake", "Parrot", "Penguin"};

            for (int i = 1; i <= memberCount; ++i) {
                memberRepository.save(
                        Member.builder()
                                .username("test"+i)
                                .password(passwordEncoder.encode("password"))
                                .name("테스터"+i)
                                .nickname(nick[i-1])
                                .birthDate(LocalDate.of(2000, 01, 01))
                                .address(new Address("32976", "충남 논산시 시민로 339", "충남 논산시 취암동 898", "예쁜글씨&예쁜디자인"))
                                .gender(i % 2 == 1 ? Gender.MALE : Gender.FEMALE)
                                .phoneNumber("010-1234-4567")
                                .point(1000000000)
                                .email("test" + i + "@prettypop.com")
                                .build()
                );
            }
        }
    }
}
