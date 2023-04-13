package prettypop.shop.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prettypop.shop.configuration.jwt.JwtTokenProvider;
import prettypop.shop.configuration.jwt.JwtTokenUtils;
import prettypop.shop.dto.auth.LoginParam;
import prettypop.shop.dto.auth.Token;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.auth.Auth;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.auth.AuthRepository;

import javax.security.auth.RefreshFailedException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    @Mock
    JwtTokenUtils jwtTokenUtils;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MemberRepository memberRepository;

    @Mock
    AuthRepository authRepository;

    @InjectMocks
    AuthService authService;

    private static final String LOGIN_ID = "test";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded_password";

    private static final String ACCESS_TOKEN = "prettypopaccesstoken";
    private static final String NEW_ACCESS_TOKEN = "new_prettypopaccesstoken";

    // 기존 DB에 저장된 REFRESH TOKEN
    private static final String REFRESH_TOKEN = "old_prettypoprefreshtoken";

    // 로그인 시 새로 갱신할 REFRESH TOKEN
    private static final String NEW_REFRESH_TOKEN = "new_prettypoprefreshtoken";

    private static final Long MEMBER_ID = 1L;
    private static final Long AUTH_ID = 50L;
    Member member;
    Auth auth;

    @BeforeEach
    void beforeEach() {
        initData();
    }

    @Test
    @DisplayName("로그인 시 AuthRepository에 사용자 정보가 없다면 리프레시 토큰을 새로 저장한다.")
    void loginTest_AuthRepositoryNotUsername() throws Exception {
        // given
        LoginParam param = new LoginParam();
        param.setUsername(LOGIN_ID);
        param.setPassword(PASSWORD);

        // mock
        when(memberRepository.findByUsername(LOGIN_ID)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtTokenProvider.createToken(MEMBER_ID, LOGIN_ID)).thenReturn(
                new Token(null, ACCESS_TOKEN, NEW_REFRESH_TOKEN, LOGIN_ID)
        );
        when(authRepository.findByUsername(LOGIN_ID)).thenReturn(Optional.empty());

        // when
        Token token = authService.login(param);

        // then
        verify(memberRepository, times(1)).findByUsername(LOGIN_ID);
        verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtTokenProvider, times(1)).createToken(MEMBER_ID, LOGIN_ID);
        verify(authRepository, times(1)).findByUsername(LOGIN_ID);
        verify(authRepository, times(1)).save(any(Auth.class));
        assertThat(token.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        assertThat(token.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(token.getKey()).isEqualTo(LOGIN_ID);
    }

    @Test
    @DisplayName("로그인 시 AuthRepository에 사용자 정보가 있다면 리프레시 토큰을 갱신한다.")
    void loginTest() throws Exception {
        // given
        LoginParam param = new LoginParam();
        param.setUsername(LOGIN_ID);
        param.setPassword(PASSWORD);

        // mock
        when(memberRepository.findByUsername(LOGIN_ID)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtTokenProvider.createToken(MEMBER_ID, LOGIN_ID)).thenReturn(
                new Token(null, ACCESS_TOKEN, NEW_REFRESH_TOKEN, LOGIN_ID)
        );
        when(authRepository.findByUsername(LOGIN_ID)).thenReturn(Optional.of(auth));

        // when
        assertThat(auth.getRefreshToken()).isEqualTo(REFRESH_TOKEN);    // 리프레시 토큰 갱신 전
        Token token = authService.login(param);

        // then
        verify(memberRepository, times(1)).findByUsername(LOGIN_ID);
        verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtTokenProvider, times(1)).createToken(MEMBER_ID, LOGIN_ID);
        verify(authRepository, times(1)).findByUsername(LOGIN_ID);
        assertThat(auth.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        assertThat(token.getRefreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
        assertThat(token.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(token.getKey()).isEqualTo(LOGIN_ID);
    }

    @Test
    @DisplayName("액세스 토큰을 재발급 받을 때 리프레시 토큰이 만료되었으면 예외가 발생한다.")
    void reissueAccessTokenTest_RefreshFailedException() throws Exception {
        // mock
        when(authRepository.findByRefreshToken(REFRESH_TOKEN)).thenReturn(Optional.of(auth));
        when(jwtTokenUtils.validateRefreshToken(REFRESH_TOKEN)).thenReturn(false);

        // assert
        assertThatThrownBy(
                () -> authService.reissueAccessToken(REFRESH_TOKEN)
        ).isInstanceOf(RefreshFailedException.class);

        verify(authRepository, times(1)).findByRefreshToken(REFRESH_TOKEN);
        verify(jwtTokenUtils, times(1)).validateRefreshToken(REFRESH_TOKEN);
    }

    private void initData() {
        member = Member.builder().username(LOGIN_ID)
                .password(ENCODED_PASSWORD)
                .build();
        ReflectionTestUtils.setField(member, "id", MEMBER_ID);

        auth = new Auth(member.getId(), LOGIN_ID, REFRESH_TOKEN);
        ReflectionTestUtils.setField(auth, "id", AUTH_ID);
    }
}