package pl.umk.mat.git2befit.service.LoginAPI;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.Entity.User;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;
import pl.umk.mat.git2befit.security.PasswordGenerator;

import java.util.Collections;
import java.util.Optional;

import static pl.umk.mat.git2befit.security.SecurityConstraints.*;
import static pl.umk.mat.git2befit.security.SecurityConstraints.TOKEN_PREFIX;

@Service
public class GoogleLogin {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public GoogleLogin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> loginUserWithGoogleToken(String idTokenString){
        Optional<Payload> payload = verifyToken(idTokenString);
        if (payload.isPresent()){
            Optional<User> user = createUserIfNotExists(payload.get());
            String tokenJWT = JWTGenerator.generate(payload.get().getEmail());
            return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + tokenJWT).build();
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    private Optional<User> createUserIfNotExists(Payload payload) {
        Optional<User> user = userRepository.findByEmail(payload.getEmail());
        if(user.isEmpty()){
            String encodedPassword = encodePassword(PasswordGenerator.generateRandomPassword());
            userRepository.save(new User(payload.getEmail(), encodedPassword));
        }
        return userRepository.findByEmail(payload.getEmail());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private Optional<Payload> verifyToken(String idTokenString){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singleton(GOOGLE_CLIENT_ID))
                .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
            return Optional.ofNullable(idToken.getPayload());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

}
