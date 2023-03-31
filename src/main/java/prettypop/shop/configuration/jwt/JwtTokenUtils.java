package prettypop.shop.configuration.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

import static prettypop.shop.configuration.jwt.TokenConst.ACCESS_TOKEN_VALIDITY;
import static prettypop.shop.configuration.jwt.TokenConst.REFRESH_TOKEN_VALIDITY;

@Slf4j
@Component
public class JwtTokenUtils {

    public Long extractMemberId(String accessToken) {
        return Long.parseLong(Jwts.parser().setSigningKey(TokenConst.ACCESS_SECRET_KEY).parseClaimsJws(accessToken).getBody().get(TokenConst.SUBJECT_ID, String.class));
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(TokenConst.ACCESS_SECRET_KEY).parseClaimsJws(accessToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(TokenConst.REFRESH_SECRET_KEY).parseClaimsJws(refreshToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return Arrays.stream(getCookies(request))
                .filter(cookie -> cookie.getName().equals(TokenConst.ACCESS_TOKEN))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return Arrays.stream(getCookies(request))
                .filter(cookie -> cookie.getName().equals(TokenConst.REFRESH_TOKEN))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);
    }

    private Cookie[] getCookies(HttpServletRequest request) {
        return request.getCookies() == null ? new Cookie[0] : request.getCookies();
    }

    public void setCookieAccessToken(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(TokenConst.ACCESS_TOKEN, accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(ACCESS_TOKEN_VALIDITY);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public void setCookieRefreshToken(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(TokenConst.REFRESH_TOKEN, refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_VALIDITY);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
}
