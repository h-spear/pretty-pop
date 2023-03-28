package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.MemberRegisterParam;
import prettypop.shop.entity.Member;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private static final int BASE_POINT = 3000;

    private final MemberRepository memberRepository;

    @Transactional
    public Long join(MemberRegisterParam param) {
        log.info("회원가입 서비스 실행");
        validateDuplicateUsername(param.getUsername());

        if (!param.getPassword().equals(param.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Member member = Member.builder()
                .username(param.getUsername())
                .password(param.getPassword())
                .name(param.getName())
                .nickname(generateRandomNickname())
                .gender(param.getGender())
                .birthDate(param.getBirthDate())
                .address(param.getAddress())
                .phoneNumber(param.getPhoneNumber())
                .email(param.getEmail())
                .point(BASE_POINT)
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private void validateDuplicateUsername(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(m -> {
                    throw new MemberUsernameDuplicateException();
                });
    }

    private String generateRandomNickname() {
        return RandomString.make(10);
    }
}
