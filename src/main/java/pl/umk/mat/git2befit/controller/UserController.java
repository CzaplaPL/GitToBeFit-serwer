package pl.umk.mat.git2befit.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.filter.JWTAuthenticationFilter;
import pl.umk.mat.git2befit.googleLoginAPI.GoogleLogin;
import pl.umk.mat.git2befit.model.User;
import pl.umk.mat.git2befit.repository.UserRepository;

import java.net.URI;
import java.util.Optional;

import static pl.umk.mat.git2befit.security.SecurityConstraints.HEADER_STRING;
import static pl.umk.mat.git2befit.security.SecurityConstraints.TOKEN_PREFIX;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private GoogleLogin googleLogin;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, GoogleLogin googleLogin) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleLogin = googleLogin;
    }

    @GetMapping("/login/google/{idToken}")
    public ResponseEntity<?> loginWithGoogle(@PathVariable String idToken){
        Optional<String> tokenJWT = googleLogin.LoginUserWithGoogleToken(idToken);
        if(tokenJWT.isPresent()){
            return ResponseEntity.ok().header(HEADER_STRING, TOKEN_PREFIX + tokenJWT.get()).build();
        }else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User tmp = userRepository.save(user);
            return ResponseEntity.created(URI.create("/user/" + tmp.getId())).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.isPresent() ?
                ResponseEntity.ok(foundUser.get()) :
                ResponseEntity.notFound().build();
    }

    /**
     * @param id   variable sent in path of request
     * @param user object sent from the application in JSON file as the body of request
     * @author KacperCzajkowski
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id,
                                    @RequestBody User user) {
        Optional<User> userFromTheDB = userRepository.findById(id);
        if (userFromTheDB.isEmpty()) {
            return ResponseEntity.notFound()
                    .build();
        } else {
            User tempUser = userFromTheDB.get();
            tempUser.setEmail(user.getEmail());
            tempUser.setPassword(passwordEncoder.encode(user.getPassword()));
            try {
                userRepository.save(tempUser);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }
}
