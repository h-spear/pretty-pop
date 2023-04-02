package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.MemberBasicDto;
import prettypop.shop.dto.MemberRegisterParam;
import prettypop.shop.dto.MemberUpdateParam;
import prettypop.shop.entity.Member;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.repository.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private static final int BASE_POINT = 3000;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(MemberRegisterParam param) {
        log.info("회원가입 서비스 실행");
        validateDuplicateUsername(param.getUsername());

        if (!param.getPassword().equals(param.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

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
        member.changePersonalInfo(param.getName(), param.getGender(), param.getBirthDate(),
                                  param.getAddress(), param.getPhoneNumber(), param.getEmail());
        return member.getId();
    }

    @Transactional
    public Long updateNickname(Long id, String nickname) {
        memberRepository.findByNickname(nickname)
                .ifPresent(m -> {
                    throw new MemberNicknameDuplicateException("이미 존재하는 닉네임입니다. " + nickname);
                });
        Member member = memberRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        member.changeNickname(nickname);
        return member.getId();
    }

    public MemberBasicDto getMemberInfo(Long id) {
        return memberRepository.findBasicInfoById(id)
                .orElseThrow(IllegalArgumentException::new);
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
