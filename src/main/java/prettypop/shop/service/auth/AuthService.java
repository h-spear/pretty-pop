package prettypop.shop.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.configuration.jwt.JwtTokenProvider;
import prettypop.shop.configuration.jwt.JwtTokenUtils;
import prettypop.shop.dto.auth.LoginParam;
import prettypop.shop.dto.auth.Token;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.auth.Auth;
import prettypop.shop.repository.MemberRepository;
import prettypop.shop.repository.auth.AuthRepository;

import javax.security.auth.RefreshFailedException;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenUtils jwtTokenUtils;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Token login(LoginParam param) {
        log.info("로그인 서비스 실행");

        String username = param.getUsername();

        Member member = memberRepository.findByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("회원 아이디가 존재하지 않습니다.");
        });

        if (!passwordEncoder.matches(param.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Token token = jwtTokenProvider.createToken(member.getId(), username);
        String refreshToken = token.getRefreshToken();

        Optional<Auth> optional = authRepository.findByUsername(username);
        if (optional.isEmpty()) {
            Auth auth = new Auth(member.getId(), username, refreshToken);
            authRepository.save(auth);
        } else {
            Auth auth = optional.get();
            auth.setRefreshToken(refreshToken);
        }
        return token;
    }

    public String reissueAccessToken(String refreshToken) throws RefreshFailedException {
        Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(RefreshFailedException::new);
        if (!jwtTokenUtils.validateRefreshToken(refreshToken)) {
            throw new RefreshFailedException();
        }
        return jwtTokenProvider.createAccessToken(auth.getUserId(), auth.getUsername(), new Date());
    }
}
