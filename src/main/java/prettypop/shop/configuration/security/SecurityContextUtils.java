package prettypop.shop.configuration.security;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import prettypop.shop.configuration.jwt.TokenConst;

@Component
@RequiredArgsConstructor
public class SecurityContextUtils {

    private final UserDetailsServiceImpl userDetailsService;

    public User getPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void setAuthentication(String accessToken) {
        Authentication authentication = getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(TokenConst.ACCESS_SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    private Authentication getAuthentication(String token) {
        User user = userDetailsService.loadUserByUsername(this.getUsername(token));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }
}
