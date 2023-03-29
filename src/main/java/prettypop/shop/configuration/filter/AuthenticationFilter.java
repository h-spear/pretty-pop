package prettypop.shop.configuration.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.GenericFilterBean;
import prettypop.shop.configuration.jwt.JwtTokenUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

    private static final String[] whiteList = {"/", "/home", "/member/join", "/login", "/logout", "/refresh",
                                                "/css/*", "/js/*", "/*.ico", "/error", "/assets/*"};

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String accessToken = jwtTokenUtils.resolveAccessToken(httpRequest);
        String refreshToken = jwtTokenUtils.resolveRefreshToken(httpRequest);

        if (accessToken != null && jwtTokenUtils.validateAccessToken(accessToken)) {
            jwtTokenUtils.securityContextSetAuthentication(accessToken);
        } else if (isAuthenticationCheckPath(requestURI)) {
            if (refreshToken != null) {
                log.info("액세스 토큰 만료. 리프레시");
                httpResponse.sendRedirect("/refresh?redirectURL=" + requestURI);
            } else {
                log.info("미인증 사용자 요청");
                httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
            }
            return;
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    private boolean isAuthenticationCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}