package pl.umk.mat.git2befit.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import pl.umk.mat.git2befit.model.account.management.FacebookUser;
import pl.umk.mat.git2befit.model.entity.User;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;
import pl.umk.mat.git2befit.security.PasswordGenerator;

import java.util.Optional;

import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.*;

@Service
public class FacebookLogin {
    private UserRepository userRepository;
    private WebClient webClient;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public FacebookLogin(UserRepository userRepository, WebClient webClient, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.webClient = webClient;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> loginWithFacebookToken(String facebookToken) {
        FacebookUser facebookUser = validateFacebookToken(facebookToken);

        if(facebookUser.getEmail().isEmpty())
            return ResponseEntity.badRequest().build();

        Optional<User> userOptional = userRepository.findByEmail(facebookUser.getEmail());

        createUserIfNotExists(facebookUser, userOptional);

        String token = JWTGenerator.generate(facebookUser.getEmail());

        return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + token)
                .header("idUser", userOptional.get().getId().toString())
                .header("email", userOptional.get().getEmail())
                .build();
    }

    private FacebookUser validateFacebookToken(String facebookToken) {
        String url = String.format(FACEBOOK_AUTH_URL, facebookToken);

        return webClient.get().uri(url).retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    throw new ResponseStatusException(clientResponse.statusCode(), "facebook login error");
                }).bodyToMono(FacebookUser.class)
                .block();
    }

    private void createUserIfNotExists(FacebookUser facebookUser, Optional<User> userOptional) {
        if (userOptional.isEmpty()){
            userRepository.save(new User(facebookUser.getEmail(), encodePassword(PasswordGenerator.generateRandomPassword()), true));
        }
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

}
