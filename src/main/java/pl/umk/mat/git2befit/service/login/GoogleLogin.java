package pl.umk.mat.git2befit.service.login;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.entity.User;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;
import pl.umk.mat.git2befit.security.PasswordGenerator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.*;
import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.TOKEN_PREFIX;

@Service
public class GoogleLogin {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(GoogleLogin.class);

    @Autowired
    public GoogleLogin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> loginUserWithGoogleToken(String idTokenString) {
        try {
            Optional<Payload> payload = verifyToken(idTokenString);
            if (payload.isPresent()) {
                Optional<User> user = createUserIfNotExists(payload.get());
                String tokenJWT = JWTGenerator.generate(payload.get().getEmail());
                return ResponseEntity.ok().header(AUTHORIZATION, TOKEN_PREFIX + tokenJWT).build();
            } else {
                return ResponseEntity.badRequest().header("Cause", "user not found").build();
            }
        } catch (GeneralSecurityException e) {
            return ResponseEntity.badRequest().header("Cause", "token verification failed").build();
        } catch (IOException e) {
            logger.error("json file parser error", e);
            return ResponseEntity.badRequest().header("Cause", "parsing failed").build();
        }
    }

    private Optional<User> createUserIfNotExists(Payload payload) {
        Optional<User> user = userRepository.findByEmail(payload.getEmail());
        if (user.isEmpty()) {
            String encodedPassword = encodePassword(PasswordGenerator.generateRandomPassword());
            userRepository.save(new User(payload.getEmail(), encodedPassword, true));
        }
        return userRepository.findByEmail(payload.getEmail());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Optional<Payload> verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singleton(GOOGLE_CLIENT_ID))
                .build();

        GoogleIdToken idToken;
        idToken = verifier.verify(idTokenString);
        if(idToken == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(idToken.getPayload());
        }
    }

}
