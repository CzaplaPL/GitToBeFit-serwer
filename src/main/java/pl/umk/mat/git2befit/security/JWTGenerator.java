package pl.umk.mat.git2befit.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

import static pl.umk.mat.git2befit.security.SecurityConstraints.EXPIRATION_TIME;
import static pl.umk.mat.git2befit.security.SecurityConstraints.SECRET;

public class JWTGenerator {
    public static String generate(String email){
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET.getBytes()));
    }
}
