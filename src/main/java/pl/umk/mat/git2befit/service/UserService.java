package pl.umk.mat.git2befit.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.exceptions.EmailValidationException;
import pl.umk.mat.git2befit.exceptions.WeakPasswordException;
import pl.umk.mat.git2befit.messaging.email.EmailMessageFacade;
import pl.umk.mat.git2befit.messaging.email.MessageGenerator;
import pl.umk.mat.git2befit.model.account.management.PasswordUpdateForm;
import pl.umk.mat.git2befit.model.entity.User;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;
import pl.umk.mat.git2befit.validation.UserValidationService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> updatePassword(long id, PasswordUpdateForm passwordUpdateForm) {
        Optional<User> savedUserOptional = userRepository.findById(id);
        if (savedUserOptional.isEmpty()) {
            return ResponseEntity.notFound().header("Cause", "user not found").build();
        }

        boolean isEquals = compareEmailAndPassword(savedUserOptional.get(), passwordUpdateForm);
        if (isEquals) {
            User user = savedUserOptional.get();
            try {
                UserValidationService.validatePassword(passwordUpdateForm.getNewPassword());
            } catch (WeakPasswordException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "new password weak").build();
            }
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));

            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "bad password").build();
        }
    }

    private boolean compareEmailAndPassword(User user, PasswordUpdateForm passwordUpdateForm) {
        return (isEmailEquals(user.getEmail(), passwordUpdateForm.getEmail()) &&
                isPasswordEquals(user.getPassword(), passwordUpdateForm.getActualPassword()));
    }

    private boolean isPasswordEquals(String savedPassword, String formActualPassword) {
        return passwordEncoder.matches(formActualPassword, savedPassword);

    }

    private boolean isEmailEquals(String email, String email2) {
        return email.equals(email2);
    }

    public ResponseEntity<?> registerUserFromApp(User user) {
        try {
            UserValidationService.validateUser(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnable(false);
            User tmp = userRepository.save(user);
            String token = JWTGenerator.generateVerificationToken(tmp.getId());
            sendEmailWithVerificationToken(tmp.getEmail(), token);

            return ResponseEntity.created(URI.create("/user/" + tmp.getId())).build();
        } catch (EmailValidationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "bad email").build();
        } catch (WeakPasswordException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "weak password").build();
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "duplicate entry").build();
        } catch (EmailException e) {
            log.error("Error while sending verification email", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
        } catch (Exception e) {
            log.error("Unexpected error occured in registerUserFromApp method", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Cause", "unexpected error").build();
        }
    }

    public ResponseEntity<User> getUserById(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().header("Cause", "user not found").build());
    }

    public ResponseEntity<?> getUserIdByEmail(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        return foundUser.map(user -> ResponseEntity.ok().header("idUser", String.valueOf(user.getId())).build())
                .orElseGet(() -> ResponseEntity.notFound().header("Cause", "user not found").build());
    }

    public ResponseEntity<?> sendNewGeneratedPasswordByEmail(String email) {
        Optional<User> dbUser = userRepository.findByEmail(email);
        if (dbUser.isEmpty()) {
            return ResponseEntity.notFound().header("Cause", "user not found").build();
        } else {
            String newPassword = RandomString.make(10);
            String message = MessageGenerator.getPasswordChangingMessage(newPassword);
            User user = dbUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            try {
//                EmailMessage emailMessage = new EmailMessage.Builder()
//                        .address(email)
//                        .subject("Przypomnienie hasła")
//                        .message(message)
//                        .build();
                EmailMessageFacade emailMessage = new EmailMessageFacade("Przypomnienie hasła", message, email);
                emailMessage.sendEmail();
                userRepository.save(user);
                return ResponseEntity.ok().build();
            } catch (EmailException e) {
                log.error("Error while sending verification email", e);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).header("save error").build();
            }
        }
    }

    public ResponseEntity<?> deleteUser(long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().header("Cause", "user not found").build();
        }
    }

    public ResponseEntity<?> updateEmail(long id, User form) {
        Optional<User> dbUser = userRepository.findById(id);
        if (dbUser.isPresent()) {
            User user = dbUser.get();
            if (isPasswordEquals(user.getPassword(), form.getPassword())) {
                try {
                    UserValidationService.validateEmail(form.getEmail());
                    user.setEmail(form.getEmail());
                    user.setEnable(false);
                    userRepository.save(user);
                    String token = JWTGenerator.generateVerificationToken(user.getId());
                    sendEmailWithVerificationToken(form.getEmail(), token);
                    return ResponseEntity.ok().build();
                } catch (EmailException e) {
                    log.error("Error while sending verification email", e);
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).header("Cause", "email sending").build();
                } catch (DataIntegrityViolationException e) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "duplicated email").build();
                } catch (EmailValidationException e) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "wrong email").build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "wrong password").build();
            }
        } else {
            return ResponseEntity.notFound().header("Cause", "user not found").build();
        }
    }

    private void sendEmailWithVerificationToken(String email, String token) throws EmailException {
//        EmailMessage msg = new EmailMessage.Builder()
//                .address(email)
//                .subject("Weryfikacja konta")
//                .message(MessageGenerator.getVerificationMessage(token))
//                .build();
        EmailMessageFacade emailToSend = new EmailMessageFacade("Weryfikacja konta",
                MessageGenerator.getVerificationMessage(token),
                email);
        emailToSend.sendEmail();
    }

    public ResponseEntity<?> activateUser(String token) {
        try {
            String id = JWT.require(Algorithm.HMAC256(SECRET.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
            Optional<User> userOptional = userRepository.findById(Long.valueOf(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!user.isEnable()) {
                    user.setEnable(true);
                    userRepository.save(user);
                }
                String msg = Files.readString(Path.of("./verification-messages/success.txt"));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
            } else {
                String msg = Files.readString(Path.of("./verification-messages/user-not-found.txt"));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
            }
        } catch (TokenExpiredException e) {
            String msg;
            try {
                msg = Files.readString(Path.of("./verification-messages/token-expired.txt"));
                return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
            } catch (IOException ioException) {
                log.error("Error occured in sending email message.", ioException);
            }
        } catch (IOException e) {
            log.error("Error occured in sending email message.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Cause", "message error").build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Cause", "unexpected error").build();
    }

    public ResponseEntity<?> verify(String token) {
        try {
            String email = JWT.require(Algorithm.HMAC256(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                String newToken = JWTGenerator.generate(email);
                return ResponseEntity.ok().header(AUTHORIZATION, TOKEN_PREFIX + newToken).build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Cause", "user not found").build();
            }
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Cause", "token is not valid").build();
        }
    }
}
