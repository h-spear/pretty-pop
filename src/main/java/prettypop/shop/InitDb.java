package prettypop.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.MemberRepository;

import javax.annotation.PostConstruct;

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
        private final PasswordEncoder passwordEncoder;

        public void dbInit() {
            int memberCount = 10;
            for (int i = 1; i <= memberCount; ++i) {
                memberRepository.save(
                        Member.builder()
                                .username("member"+i)
                                .name("name" + i)
                                .nickname("nick" + i)
                                .point(i)
                                .password(passwordEncoder.encode("password"))
                                .build()
                );
            }
        }
    }
}
