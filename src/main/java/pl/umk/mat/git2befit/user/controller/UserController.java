package pl.umk.mat.git2befit.user.controller;

import org.apache.commons.mail.EmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.user.model.management.APIAuthModel;
import pl.umk.mat.git2befit.user.model.management.LoginForm;
import pl.umk.mat.git2befit.user.model.entity.User;
import pl.umk.mat.git2befit.user.model.management.PasswordUpdateForm;
import pl.umk.mat.git2befit.user.service.login.FacebookLogin;
import pl.umk.mat.git2befit.user.service.login.GoogleLogin;
import pl.umk.mat.git2befit.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private GoogleLogin googleLogin;
    private FacebookLogin facebookLogin;
    private UserService userService;

    public UserController(GoogleLogin googleLogin, FacebookLogin facebookLogin, UserService userService) {
        this.googleLogin = googleLogin;
        this.facebookLogin = facebookLogin;
        this.userService = userService;
    }

    @PostMapping("/login/facebook")
    public ResponseEntity<?> loginWithFacebook(@RequestBody APIAuthModel fbModel) {
        return facebookLogin.loginWithFacebookToken(fbModel.getIdToken());
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody APIAuthModel googleAuthModel) {
        return googleLogin.loginUserWithGoogleToken(googleAuthModel.getIdToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginWithApp(@RequestBody LoginForm loginForm) {
        return userService.loginUser(loginForm);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        try {
            return userService.registerUserFromApp(user);
        } catch (EmailException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/search/{email}")
    public ResponseEntity<?> getUserIdByEmail(@PathVariable String email) {
        return userService.getUserIdByEmail(email);
    }

    @PutMapping("/{id}/password-update")
    public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody PasswordUpdateForm form) {
        return userService.updatePassword(id, form);
    }

    @PutMapping("/{id}/email-update")
    public ResponseEntity<?> changeEmail(@PathVariable long id, @RequestBody User form) {
        try {
            return userService.updateEmail(id, form);
        } catch (EmailException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
        }
    }

    @GetMapping("/activation/{token}")
    public ResponseEntity<?> activateUser(@PathVariable String token) {
        return userService.activateUser(token);
    }

    @PostMapping("/remind-password")
    public ResponseEntity<?> remindPassword(@RequestParam String email) {
        try {
            return userService.sendNewGeneratedPasswordByEmail(email);
        } catch (EmailException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @RequestHeader String password) {
        return userService.deleteUser(id, password);
    }

    @PostMapping("/token-verification")
    public ResponseEntity<?> verify(@RequestHeader String token){
        return userService.verify(token);
    }

    @PostMapping("/activation/renew")
    public ResponseEntity<?> sendAgainActivation(@RequestHeader String email){
        return userService.sendAgainActivationToken(email);
    }

}
