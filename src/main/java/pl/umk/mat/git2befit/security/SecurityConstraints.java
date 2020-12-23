package pl.umk.mat.git2befit.security;

public class SecurityConstraints {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/signup";
    public static final String FACEBOOK_AUTH_URL = "https://graph.facebook.com/me?fields=email&access_token=%s";
    public static final String FACEBOOK_LOGIN = "/user/login/facebook";
    public static final String GOOGLE_LOGIN = "/user/login/google";

}
