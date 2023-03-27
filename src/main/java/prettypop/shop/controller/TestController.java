package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import prettypop.shop.entity.Gender;
import prettypop.shop.entity.Member;
import prettypop.shop.configuration.jwt.JwtTokenProvider;
import prettypop.shop.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @GetMapping("/test")
    public String test() {
        return "test...";
    }

    @GetMapping("/user/logic")
    public String user() {
        return "user!";
    }

    @GetMapping("/admin/logic")
    public String admin() {
        return "admin user!";
    }

    final String USERNAME = "member";
    final LocalDate BIRTH = LocalDate.of(1998, 8, 27);
    final String EMAIL = "member@test.com";
    final String NICKNAME = "rabbit";
    final String NAME = "래빗";
    final Gender GENDER = Gender.MALE;

    Member member = Member.builder()
            .username(USERNAME)
            .email(EMAIL)
            .birthDate(BIRTH)
            .nickname(NICKNAME)
            .name(NAME)
            .gender(GENDER)
            .build();

    @GetMapping("/join")
    public Long join(){
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    // 로그인
    @GetMapping("/login")
    public String login(@RequestBody Map<String, String> map) {
        Member member = memberRepository.findByUsername(map.get("username"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        log.info("login id={}, username={}", member.getId(), map.get("username"));
        return jwtTokenProvider.createToken(member.getUsername(), member.getRoles());
    }
}
