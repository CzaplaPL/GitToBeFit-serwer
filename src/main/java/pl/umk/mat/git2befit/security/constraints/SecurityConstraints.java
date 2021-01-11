package pl.umk.mat.git2befit.security.constraints;

public class SecurityConstraints {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final long VERIFICATION_TOKEN_EXPIRATION_TIME = 86_400_000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/signup";
    public static final String FACEBOOK_AUTH_URL = "https://graph.facebook.com/me?fields=email&access_token=%s";
    public static final String FACEBOOK_LOGIN = "/user/login/facebook";
    public static final String GOOGLE_LOGIN = "/user/login/google";
    public static final String GOOGLE_CLIENT_ID = "167652090961-5dkah0ddinbeh8clnq81ieg3h2onkvjp.apps.googleusercontent.com";
    public static final String EMAIL_VERIFICATION = "/user/activation/{token}";
    public static final String PASSWORD_REMIND = "/user/remind-password";
    private static final String STARY_GOOGLE_CLIENT_ID = "57028483820-2fdhd5l8e0vc3knl5o3urda3n2di4gu9.apps.googleusercontent.com";


}
