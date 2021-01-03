package pl.umk.mat.git2befit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.Entity.User;
import pl.umk.mat.git2befit.model.APIAuthModel;
import pl.umk.mat.git2befit.model.PasswordUpdateForm;
import pl.umk.mat.git2befit.service.LoginAPI.FacebookLogin;
import pl.umk.mat.git2befit.service.LoginAPI.GoogleLogin;
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
    public ResponseEntity<Long> getUserByEmail(@PathVariable String email) {
        return userService.getUserIDByEmail(email);
    }

    @PutMapping("/{id}/password-update")
    public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody PasswordUpdateForm form) {
        return userService.updatePassword(id, form);
    }

    @PostMapping("/remind-password/{email}")
    public ResponseEntity<?> remindPassword(@PathVariable String email) {
        return userService.sendNewGeneratedPasswordByEmail(email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        return userService.deleteUser(id);
    }
}
