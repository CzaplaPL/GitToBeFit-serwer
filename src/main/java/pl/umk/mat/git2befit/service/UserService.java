package pl.umk.mat.git2befit.service;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.exceptions.WeakPasswordException;
import pl.umk.mat.git2befit.messaging.email.EmailMessage;
import pl.umk.mat.git2befit.messaging.email.MessageGenerator;
import pl.umk.mat.git2befit.model.Entity.User;
import pl.umk.mat.git2befit.model.PasswordUpdateForm;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.PasswordGenerator;
import pl.umk.mat.git2befit.validation.UserValidationService;

import java.net.URI;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserValidationService userValidationService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserValidationService userValidationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidationService = userValidationService;
    }

    public ResponseEntity<?> updatePassword(long id, PasswordUpdateForm passwordUpdateForm) {
        Optional<User> savedUserOptional = userRepository.findById(id);
        if (savedUserOptional.isEmpty())
            return ResponseEntity.notFound().build();

        boolean isEquals = compareEmailAndPassword(savedUserOptional.get(), passwordUpdateForm);

        if (isEquals) {
            try {
                userValidationService.validatePassword(passwordUpdateForm.getNewPassword());
            } catch (WeakPasswordException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
            }
            User user = savedUserOptional.get();
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));

            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private boolean compareEmailAndPassword(User user, PasswordUpdateForm passwordUpdateForm) {
        return (isEmailEquals(user.getEmail(), passwordUpdateForm.getEmail()) && isPasswordEquals(user.getPassword(), passwordUpdateForm.getActualPassword()));
    }

    private boolean isPasswordEquals(String savedPassword, String formActualPassword) {
        return passwordEncoder.matches(formActualPassword, savedPassword);

    }

    private boolean isEmailEquals(String email, String email2) {
        return email.equals(email2);
    }

    public ResponseEntity<?> registerUserFromApp(User user) {
        try {
            userValidationService.validateUser(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User tmp = userRepository.save(user);
            return ResponseEntity.created(URI.create("/user/" + tmp.getId())).build();
        } catch (EmailException | WeakPasswordException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", e.getMessage()).build();
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "duplicate entry").build();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<User> getUserById(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                        .build());
    }

    public ResponseEntity<User> getUserByEmail(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound()
                        .build());
    }

    public ResponseEntity<?> sendNewGeneratedPasswordByEmail(String email) {
        Optional<User> dbUser = userRepository.findByEmail(email);
        ResponseEntity<?> response;
        if (dbUser.isEmpty()) {
            response = ResponseEntity.notFound().build();
        } else {
            String newPassword = PasswordGenerator.generateRandomPassword().substring(0,14);
            String message = MessageGenerator.getPasswordChangingMessage(newPassword);
            User user = dbUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            try {
                userRepository.save(user);

                EmailMessage emailMessage = new EmailMessage.Builder()
                        .address(email)
                        .subject("Przypomnienie hasła")
                        .message(message)
                        .build();
                emailMessage.sendEmail();
                response = ResponseEntity.ok().build();
            }catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } catch (EmailException e) {
                response = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
            }
        }
        return response;
    }

    public ResponseEntity<?> deleteUser(long id) {
        ResponseEntity<?> responseEntity;
        try {
            userRepository.deleteById(id);
            responseEntity = ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            responseEntity = ResponseEntity.notFound().build();
        }
        return responseEntity;
    }
}
