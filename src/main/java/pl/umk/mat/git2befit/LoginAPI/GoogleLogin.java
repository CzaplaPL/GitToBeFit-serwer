package pl.umk.mat.git2befit.LoginAPI;

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
import pl.umk.mat.git2befit.model.User;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;

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

    public ResponseEntity<?> LoginUserWithGoogleToken(String idTokenString){
        Optional<Payload> payload = verifyToken(idTokenString);
        if (payload.isPresent()){
            createUserIfNotExists(payload.get());
            String tokenJWT = JWTGenerator.generate(payload.get().getEmail());
            return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + tokenJWT).build();
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private void createUserIfNotExists(Payload payload) {
        Optional<User> user = userRepository.findByEmail(payload.getEmail());
        if(user.isEmpty()){
            String encodedPassword = encodePassword(new RandomString(10).nextString());
            userRepository.save(new User(payload.getEmail(), encodedPassword));
        }
    }

    private String encodePassword(String password) {

        return passwordEncoder.encode(password);
    }

    private Optional<Payload> verifyToken(String idTokenString){
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory()).build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
            return Optional.ofNullable(idToken.getPayload());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

}
