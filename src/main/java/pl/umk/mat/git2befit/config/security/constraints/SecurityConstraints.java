package pl.umk.mat.git2befit.config.security.constraints;

public interface SecurityConstraints {
    String SECRET = "SecretKeyToGenJWTs";
    long EXPIRATION_TIME = 864_000_000; // 10 days
    long VERIFICATION_TOKEN_EXPIRATION_TIME = 86_400_000; // 1 day
    String TOKEN_PREFIX = "Bearer ";
    String AUTHORIZATION = "Authorization";
    String SIGN_UP_URL = "/user/signup";
    String FACEBOOK_AUTH_URL = "https://graph.facebook.com/me?fields=email&access_token=%s";
    String FACEBOOK_LOGIN = "/user/login/facebook";
    String GOOGLE_LOGIN = "/user/login/google";
    String GOOGLE_CLIENT_ID = "167652090961-5dkah0ddinbeh8clnq81ieg3h2onkvjp.apps.googleusercontent.com";
    String EMAIL_VERIFICATION = "/user/activation/{token}";
    String PASSWORD_REMIND = "/user/remind-password";
    String TOKEN_VERIFICATION = "/user/token-verification";
    String USER_ACTIVATION_VERIFICATION = "/user/activation";
    String SEND_RENEW = "/user/activation/renew";

    String ALL_EQUIPMENTS = "/equipment";
    String NO_EQUIPMENT = "/equipment/no-equipment";
    String ALL_EQUIPMENT_TYPES = "/equipment-type";

    String TRAINING_GENERATION = "/training-plan/generate";
    String TRAINING_MODIFICATION = "/replace/{id}";
    String CHECKSUM = "/check-sum";
}
