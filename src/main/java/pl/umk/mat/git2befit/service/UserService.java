package pl.umk.mat.git2befit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.Entity.User;
import pl.umk.mat.git2befit.model.PasswordUpdateForm;
import pl.umk.mat.git2befit.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity updatePassword(long id, PasswordUpdateForm passwordUpdateForm) {
        Optional<User> savedUserOptional = userRepository.findById(id);
        if(savedUserOptional.isEmpty())
            return ResponseEntity.notFound().build();

        boolean isEquals = compareEmailAndPassword(savedUserOptional.get(), passwordUpdateForm);

        if (isEquals){
            User user = savedUserOptional.get();
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));

            userRepository.save(user);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    private boolean compareEmailAndPassword(User user, PasswordUpdateForm passwordUpdateForm) {
        return (isEmailEquals(user.getEmail(), passwordUpdateForm.getEmail()) && isPasswordEquals(user.getPassword(),passwordUpdateForm.getActualPassword()));
    }

    private boolean isPasswordEquals(String savedPassword, String formActualPassword) {
        return passwordEncoder.matches(formActualPassword, savedPassword);

    }

    private boolean isEmailEquals(String email, String email2) {
        return email.equals(email2);
    }
}
