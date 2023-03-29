package prettypop.shop.configuration.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import prettypop.shop.dto.auth.Token;

import java.util.Date;

import static prettypop.shop.configuration.jwt.TokenConst.ACCESS_TOKEN_VALIDITY;
import static prettypop.shop.configuration.jwt.TokenConst.REFRESH_TOKEN_VALIDITY;

@Slf4j
@Component
public class JwtTokenProvider {

    public Token createToken(String username) {
        log.info("username={}", username);

        Date now = new Date();

        String accessToken = createAccessToken(username, now);
        String refreshToken = createRefreshToken(username, now);

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(username).build();
    }

    public String createAccessToken(String username, Date iat) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + ACCESS_TOKEN_VALIDITY * 1000L))
                .signWith(SignatureAlgorithm.HS256, TokenConst.ACCESS_SECRET_KEY)
                .compact();
    }

    private String createRefreshToken(String username, Date iat) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(iat)
                .setExpiration(new Date(iat.getTime() + REFRESH_TOKEN_VALIDITY * 1000L))
                .signWith(SignatureAlgorithm.HS256, TokenConst.REFRESH_SECRET_KEY)
                .compact();
    }
}
