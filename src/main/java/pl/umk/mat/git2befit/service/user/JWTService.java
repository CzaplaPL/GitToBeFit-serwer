package pl.umk.mat.git2befit.service.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.SECRET;
import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.TOKEN_PREFIX;

public class JWTService {

    private JWTService() {
    }

    public static String parseEmail(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }
}
