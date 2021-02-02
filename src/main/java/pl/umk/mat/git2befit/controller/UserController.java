package pl.umk.mat.git2befit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.account.management.APIAuthModel;
import pl.umk.mat.git2befit.model.entity.User;
import pl.umk.mat.git2befit.model.account.management.PasswordUpdateForm;
import pl.umk.mat.git2befit.service.login.FacebookLogin;
import pl.umk.mat.git2befit.service.login.GoogleLogin;
import pl.umk.mat.git2befit.service.UserService;

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

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        return userService.registerUserFromApp(user);
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
        return userService.updateEmail(id, form);
    }

    @GetMapping("/activation/{token}")
    public ResponseEntity<?> activateUser(@PathVariable String token) {
        return userService.activateUser(token);
    }

    @PostMapping("/remind-password")
    public ResponseEntity<?> remindPassword(@RequestParam String email) {
        return userService.sendNewGeneratedPasswordByEmail(email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/token-verification")
    public ResponseEntity<?> verify(@RequestHeader String token){
        return userService.verify(token);
    }
}
