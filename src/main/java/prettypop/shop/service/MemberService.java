package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.member.MemberDto;
import prettypop.shop.dto.member.MemberRegisterParam;
import prettypop.shop.dto.member.MemberUpdateParam;
import prettypop.shop.entity.Member;
import prettypop.shop.exception.MemberEmailDuplicateException;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.exception.PasswordConfirmNotMatchException;
import prettypop.shop.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    // 계정 생성 시 지급되는 포인트
    @Value("${application.join-base-point}")
    private static int BASE_POINT;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(MemberRegisterParam param) {
        log.info("회원가입 서비스 실행");
        if (!param.getPassword().equals(param.getPasswordConfirm())) {
            throw new PasswordConfirmNotMatchException();
        }

        validateDuplicateUsername(param.getUsername());
        validateDuplicateEmail(param.getEmail());

        String encodedPassword = passwordEncoder.encode(param.getPassword());

        Member member = Member.builder()
                .username(param.getUsername())
                .password(encodedPassword)
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

    @Transactional
    public Long update(Long id, MemberUpdateParam param) {
        Member member = memberRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        if (!member.getEmail().equals(param.getEmail())) {
            memberRepository.findByEmail(param.getEmail())
                    .ifPresent(m -> {
                        throw new MemberEmailDuplicateException();
                    });
        }
        member.changePersonalInfo(param.getName(), param.getGender(), param.getBirthDate(),
                                  param.getAddress(), param.getPhoneNumber(), param.getEmail());
        return member.getId();
    }

    @Transactional
    public Long updateNickname(Long id, String nickname) {
        memberRepository.findByNickname(nickname)
                .ifPresent(m -> {
                    throw new MemberNicknameDuplicateException();
                });
        Member member = memberRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        member.changeNickname(nickname);
        return member.getId();
    }

    public MemberDto getMemberInfo(Long id) {
        return memberRepository.findBasicInfoById(id)
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateDuplicateUsername(String username) {
        memberRepository.findByUsername(username)
                .ifPresent(m -> {
                    throw new MemberUsernameDuplicateException();
                });
    }

    private void validateDuplicateEmail(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new MemberEmailDuplicateException();
                });
    }

    private String generateRandomNickname() {
        return RandomString.make(10);
    }

}
