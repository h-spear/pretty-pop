package prettypop.shop.configuration.jwt;

import java.util.Base64;

public class TokenConst {
    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh_Token";

    public static final int ACCESS_TOKEN_VALIDITY = 30 * 60;
    public static final int REFRESH_TOKEN_VALIDITY = 14 * 24 * 60 * 60;

    private static final String ACCESS_SECRET_KEY_NOT_ENCODED = "prettypopaccesssecret";
    private static final String REFRESH_SECRET_KEY_NOT_ENCODED = "prettypoprefreshsecret";

    public static final String ACCESS_SECRET_KEY = Base64.getEncoder().encodeToString(ACCESS_SECRET_KEY_NOT_ENCODED.getBytes());
    public static final String REFRESH_SECRET_KEY = Base64.getEncoder().encodeToString(REFRESH_SECRET_KEY_NOT_ENCODED.getBytes());
}
