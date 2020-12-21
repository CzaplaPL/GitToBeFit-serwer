package pl.umk.mat.git2befit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.umk.mat.git2befit.model.User;
import pl.umk.mat.git2befit.repository.UserRepository;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        if (foundUser.isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User tmp = userRepository.save(user);
            return ResponseEntity.created(URI.create("/user/" + tmp.getId())).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id) {
        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            return ResponseEntity.ok(foundUser.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * @param id   variable sent in path of request
     * @param user object sent from the application in JSON file as the body of request
     *             author KacperCzajkowski
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
            userRepository.save(tempUser);
            return ResponseEntity.ok().build();
        }
    }
}
