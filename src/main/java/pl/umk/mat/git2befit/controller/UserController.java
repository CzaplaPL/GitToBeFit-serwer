package pl.umk.mat.git2befit.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.User;
import pl.umk.mat.git2befit.repository.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable long id){
        return userRepository.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id,
                        @RequestBody User user){
        Optional<User> userFromTheDB = userRepository.findById(id);
        if (userFromTheDB.isEmpty()) {
            //TODO Tu do rzucenia wyjątek ale nie wiem jaki (Kacper)
        } else {
            User tempUser = userFromTheDB.get();
            tempUser.setEmail(user.getEmail());
            tempUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(tempUser);
        }
    }
}
