package pl.umk.mat.git2befit.controller;


import org.springframework.dao.DataIntegrityViolationException;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.mail.EmailException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import pl.umk.mat.git2befit.service.LoginAPI.FacebookLogin;
import pl.umk.mat.git2befit.service.LoginAPI.GoogleLogin;
import pl.umk.mat.git2befit.model.FacebookAuthModel;
import pl.umk.mat.git2befit.model.GoogleAuthModel;
import pl.umk.mat.git2befit.model.Entity.User;
import pl.umk.mat.git2befit.model.PasswordUpdateForm;

import pl.umk.mat.git2befit.messaging.email.EmailMessage;
import pl.umk.mat.git2befit.messaging.email.MessageGenerator;
import pl.umk.mat.git2befit.model.User;

import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.service.UserService;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private GoogleLogin googleLogin;
    private FacebookLogin facebookLogin;
    private UserService userService;


    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, GoogleLogin googleLogin,
                          FacebookLogin facebookLogin, UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.googleLogin = googleLogin;
        this.facebookLogin = facebookLogin;
        this.userService = userService;
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<?> loginWithFacebook(@RequestBody FacebookAuthModel fbModel){
        return facebookLogin.loginWithFacebookToken(fbModel.getToken());
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleAuthModel googleAuthModel){
        return googleLogin.LoginUserWithGoogleToken(googleAuthModel.getIdToken());
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
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                .build());
    }

    @GetMapping("/search/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                .build());
    }


    @PutMapping("/{id}/passwordUpdate")
    public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody PasswordUpdateForm form){
        return userService.updatePassword(id, form);
    }

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

    @PostMapping("/remind-password/{email}")
    public ResponseEntity<?> remindPassword(@PathVariable String email) {
        Optional<User> dbUser = userRepository.findByEmail(email);
        ResponseEntity<?> response;
        if (dbUser.isEmpty()) {
            response = ResponseEntity.notFound().build();
        } else {
            String newPassword = RandomString.make(10);
            String message = MessageGenerator.getPasswordChangingMessage(newPassword);
            User user = dbUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            try {
                userRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            try {
                EmailMessage emailMessage = new EmailMessage.Builder()
                        .address(email)
                        .subject("Przypomnienie hasła")
                        .message(message)
                        .build();
                emailMessage.sendEmail();
            } catch (EmailException e) {
                return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
            }
            response = ResponseEntity.ok().build();
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        ResponseEntity<?> responseEntity;
        try{
            userRepository.deleteById(id);
            responseEntity = ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            responseEntity = ResponseEntity.notFound().build();
        }
        return responseEntity;
    }
}
