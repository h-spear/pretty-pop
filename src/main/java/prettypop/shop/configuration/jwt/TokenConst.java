package prettypop.shop.configuration.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenConst {
    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh_Token";
    public static final String SUBJECT_ID = "subjectId";

    public static final int ACCESS_TOKEN_VALIDITY = 30 * 60;
    public static final int REFRESH_TOKEN_VALIDITY = 14 * 24 * 60 * 60;

    public static String ACCESS_SECRET_KEY;
    public static String REFRESH_SECRET_KEY;

    @Value("${my-security.access-secret-key}")
    public void setAccessSecretKeyNotEncoded(String accessSecretKey) {
        ACCESS_SECRET_KEY = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
    }

    @Value("${my-security.refresh-secret-key}")
    public void setRefreshSecretKeyNotEncoded(String refreshSecretKey) {
        REFRESH_SECRET_KEY = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }
}
