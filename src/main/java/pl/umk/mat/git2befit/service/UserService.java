package pl.umk.mat.git2befit.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.messaging.email.EmailMessage;
import pl.umk.mat.git2befit.messaging.email.MessageGenerator;
import pl.umk.mat.git2befit.model.entity.User;
import pl.umk.mat.git2befit.model.account.management.PasswordUpdateForm;
import pl.umk.mat.git2befit.repository.UserRepository;
import pl.umk.mat.git2befit.security.JWTGenerator;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static pl.umk.mat.git2befit.security.constraints.SecurityConstraints.SECRET;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<?> updatePassword(long id, PasswordUpdateForm passwordUpdateForm) {
        Optional<User> savedUserOptional = userRepository.findById(id);
        if (savedUserOptional.isEmpty())
            return ResponseEntity.notFound().build();

        boolean isEquals = compareEmailAndPassword(savedUserOptional.get(), passwordUpdateForm);
        if (isEquals) {
            User user = savedUserOptional.get();
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getNewPassword()));

            userRepository.save(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("Cause", "bad password").build();
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
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnable(false);
            User tmp = userRepository.save(user);
            String token = JWTGenerator.generateVerificationToken(tmp.getId());
            sendEmailWithVerificationToken(tmp.getEmail(), token);

            return ResponseEntity.created(URI.create("/user/" + tmp.getId())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<User> getUserById(long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> getUserIdByEmail(String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);
        return foundUser.isPresent() ?
                ResponseEntity.ok().header("idUser", String.valueOf(foundUser.get().getId())).build() :
                ResponseEntity.notFound().build();
    }

    public ResponseEntity<?> sendNewGeneratedPasswordByEmail(String email) {
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

                EmailMessage emailMessage = new EmailMessage.Builder()
                        .address(email)
                        .subject("Przypomnienie hasła")
                        .message(message)
                        .build();
                emailMessage.sendEmail();
                response = ResponseEntity.ok().build();
            } catch (EmailException e) {
                response = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
            } catch (DataIntegrityViolationException e) {
                response = ResponseEntity.status(HttpStatus.CONFLICT).build();
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

    public ResponseEntity<?> updateEmail(long id, User form) {
        Optional<User> dbUser = userRepository.findById(id);
        ResponseEntity<?> response;
        if (dbUser.isPresent()) {
            User user = dbUser.get();
            if (isPasswordEquals(user.getPassword(), form.getPassword())) {
                user.setEmail(form.getEmail());
                user.setEnable(false);
                try {
                    userRepository.save(user);
                    String token = JWTGenerator.generateVerificationToken(user.getId());
                    sendEmailWithVerificationToken(form.getEmail(), token);
                    response = ResponseEntity.ok().build();
                } catch (EmailException e) {
                    response = ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).build();
                } catch (DataIntegrityViolationException e) {
                    response = ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            } else {
                response = ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            response = ResponseEntity.notFound().build();
        }
        return response;
    }

    private void sendEmailWithVerificationToken(String email, String token) throws EmailException {
        EmailMessage msg = new EmailMessage.Builder()
                .address(email)
                .subject("Weryfikacja konta")
                .message(MessageGenerator.getVerificationMessage(token))
                .build();
        msg.sendEmail();
    }

    //TODO dodanie wersji jezykowych i wyciagania z requestheadera jaki jezyk ma byc
    public ResponseEntity<?> activateUser(String token) {
        ResponseEntity<?> response = null;
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
                String msg = Files.readString(Path.of("src/main/resources/verification.messages/success.txt"));
                response = ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
            } else {
                String msg = Files.readString(Path.of("src/main/resources/verification.messages/user-not-found.txt"));
                response = ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
            }
        } catch (TokenExpiredException e) {
            String msg = null;
            try {
                msg = Files.readString(Path.of("src/main/resources/verification.messages/token-expired.txt"));
            } catch (IOException ioException) {}
            response = ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(msg);
        } catch (IOException e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }
}
