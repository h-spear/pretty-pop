package prettypop.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.dto.member.MemberRegisterParam;
import prettypop.shop.dto.member.MemberUpdateParam;
import prettypop.shop.entity.Address;
import prettypop.shop.entity.Gender;
import prettypop.shop.entity.Member;
import prettypop.shop.exception.MemberEmailDuplicateException;
import prettypop.shop.exception.MemberNicknameDuplicateException;
import prettypop.shop.exception.MemberUsernameDuplicateException;
import prettypop.shop.exception.PasswordConfirmNotMatchException;
import prettypop.shop.repository.MemberRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private static final String USERNAME = "test";
    private static final String NAME = "Issac";
    private static final String NICKNAME = "Rabbit";
    private static final String PASSWORD = "encodedPassword";
    private static final String EMAIL = "encodedPassword";
    private static final Address ADDRESS = new Address("12345", "RoadAddress", "JibunAddress", "Detail");
    private static final Gender GENDER = Gender.MALE;
    private static final String PHONE_NUMBER = "010-1234-5678";
    private static final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 1);
    private static final int BASE_POINT = 1000000000;

    @Test
    @DisplayName("회원가입 시 중복된 아이디가 있으면 예외가 발생한다")
    void joinTest_MemberUsernameDuplicateException() throws Exception {
        // given
        MemberRegisterParam param = getRegisterParam();
        Member oldUser = Member.builder().username(USERNAME).build();

        // mock
        when(memberRepository.findByUsername(USERNAME)).thenReturn(Optional.of(oldUser));

        // assert
        assertThatThrownBy(() -> memberService.join(param))
                .isInstanceOf(MemberUsernameDuplicateException.class);
    }

    @Test
    @DisplayName("회원가입 시 중복된 이메일이 있으면 예외가 발생한다")
    void joinTest_MemberEmailDuplicateException() throws Exception {
        // given
        MemberRegisterParam param = getRegisterParam();
        Member oldUser = Member.builder().username("oldUser").email(EMAIL).build();

        // mock
        when(memberRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.of(oldUser));

        // assert
        assertThatThrownBy(() -> memberService.join(param))
                .isInstanceOf(MemberEmailDuplicateException.class);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호와 비밀번호 확인을 다르게 입력하면 예외가 발생한다")
    void joinTest_PasswordConfirmNotMatchException() throws Exception {
        // given
        MemberRegisterParam param = new MemberRegisterParam();
        param.setUsername(USERNAME);
        param.setEmail(EMAIL);
        param.setPassword("password!");
        param.setPasswordConfirm("password@");

        // assert
        assertThatThrownBy(() -> memberService.join(param))
                .isInstanceOf(PasswordConfirmNotMatchException.class);
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void joinTest_Success() throws Exception {
        // given
        Long fakeMemberId = 1L;
        MemberRegisterParam param = getRegisterParam();
        Member member = Member.builder().username(USERNAME).name(NAME).password(PASSWORD).email(EMAIL)
                .address(ADDRESS).gender(GENDER).phoneNumber(PHONE_NUMBER).birthDate(BIRTH_DATE).point(BASE_POINT)
                .build();
        ReflectionTestUtils.setField(member, "id", fakeMemberId);

        // mock
        when(passwordEncoder.encode("password!")).thenReturn(PASSWORD);
        when(memberRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findById(fakeMemberId)).thenReturn(Optional.of(member));

        // when
        Long id = memberService.join(param);

        // then
        Member findMember = memberRepository.findById(id).get();

        assertThat(id).isEqualTo(fakeMemberId);
        assertThat(findMember.getUsername()).isEqualTo(USERNAME);
        assertThat(findMember.getPassword()).isEqualTo(PASSWORD);
        assertThat(findMember.getName()).isEqualTo(NAME);
        assertThat(findMember.getPoint()).isEqualTo(BASE_POINT);
        assertThat(findMember.getEmail()).isEqualTo(EMAIL);
        assertThat(findMember.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(findMember.getAddress()).isEqualTo(ADDRESS);
        assertThat(findMember.getGender()).isEqualTo(GENDER);
        assertThat(findMember.getBirthDate()).isEqualTo(BIRTH_DATE);
    }

    @Test
    @DisplayName("개인정보 변경 시 중복된 이메일이 있으면 예외가 발생한다")
    void updateTest_MemberEmailDuplicateException() throws Exception {
        // given
        MemberUpdateParam param = new MemberUpdateParam();
        param.setEmail("new@pretty.com");

        Long fakeMemberId = 1L;
        Member member = Member.builder()
                .username(USERNAME).email(EMAIL).build();
        ReflectionTestUtils.setField(member, "id", fakeMemberId);

        Member another = Member.builder().username("another").email("new@pretty.com").build();

        // mock
        when(memberRepository.findById(fakeMemberId)).thenReturn(Optional.of(member));
        when(memberRepository.findByEmail("new@pretty.com")).thenReturn(Optional.of(another));

        // assert
        assertThatThrownBy(() -> memberService.update(fakeMemberId, param))
                .isInstanceOf(MemberEmailDuplicateException.class);
    }

    @Test
    @DisplayName("개인정보 변경에 성공한다")
    void updateTest_Success() throws Exception {
        // given
        Long fakeMemberId = 1L;
        Member member = Member.builder()
                .username(USERNAME).gender(GENDER).birthDate(BIRTH_DATE)
                .address(ADDRESS).phoneNumber(PHONE_NUMBER).email(EMAIL).build();
        ReflectionTestUtils.setField(member, "id", fakeMemberId);

        MemberUpdateParam param = new MemberUpdateParam();
        param.setName("avril");
        param.setGender(Gender.FEMALE);
        param.setBirthDate(LocalDate.of(1999, 12, 14));
        param.setPhoneNumber("010-8765-4321");
        param.setAddress(new Address("54321", "new", "newJibun", null));
        param.setEmail("new@pretty.com");

        // mock
        when(memberRepository.findById(fakeMemberId)).thenReturn(Optional.of(member));
        when(memberRepository.findByEmail("newEmail")).thenReturn(Optional.empty());

        // when
        Long memberId = memberService.update(fakeMemberId, param);

        // then
        Member findMember = memberRepository.findById(memberId).get();
        assertThat(findMember.getName()).isEqualTo("avril");
        assertThat(findMember.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(findMember.getBirthDate()).isEqualTo(LocalDate.of(1999, 12, 14));
        assertThat(findMember.getPhoneNumber()).isEqualTo("010-8765-4321");
        assertThat(findMember.getAddress()).isEqualTo(new Address("54321", "new", "newJibun", null));
        assertThat(findMember.getEmail()).isEqualTo("new@pretty.com");
    }

    @Test
    @DisplayName("닉네임 변경 시 이미 존재하는 닉네임이면 예외가 발생한다")
    void updateNicknameTest_MemberNicknameDuplicateException() throws Exception {
        // given
        Long fakeMemberId = 1L;
        Member oldUser = Member.builder().username("oldUser").password(PASSWORD).nickname(NICKNAME).build();

        // mock
        when(memberRepository.findByNickname(NICKNAME)).thenReturn(Optional.of(oldUser));

        // assert
        assertThatThrownBy(() -> memberService.updateNickname(fakeMemberId, NICKNAME))
                .isInstanceOf(MemberNicknameDuplicateException.class);
    }

    @Test
    @DisplayName("닉네임 변경에 성공한다")
    void updateNicknameTest_Success() throws Exception {
        // given
        Long fakeMemberId = 1L;
        Member member = Member.builder().username("member").password(PASSWORD).nickname(NICKNAME).build();
        ReflectionTestUtils.setField(member, "id", fakeMemberId);
        String newNickname = "Tiger";

        // mock
        when(memberRepository.findByNickname(NICKNAME)).thenReturn(Optional.empty());
        when(memberRepository.findById(fakeMemberId)).thenReturn(Optional.of(member));

        // when
        assertThat(memberRepository.findById(fakeMemberId).get().getNickname()).isEqualTo(NICKNAME); // 변경 전
        Long id = memberService.updateNickname(fakeMemberId, newNickname);

        // then
        Member afterMember = memberRepository.findById(fakeMemberId).get();
        assertThat(afterMember.getNickname()).isEqualTo(newNickname); // 변경 후
    }

    private static MemberRegisterParam getRegisterParam() {
        MemberRegisterParam param = new MemberRegisterParam();
        param.setUsername(USERNAME);
        param.setName(NAME);
        param.setPassword("password!");
        param.setPasswordConfirm("password!");
        param.setEmail(EMAIL);
        param.setAddress(ADDRESS);
        param.setGender(GENDER);
        param.setPhoneNumber(PHONE_NUMBER);
        param.setBirthDate(BIRTH_DATE);
        return param;
    }
}