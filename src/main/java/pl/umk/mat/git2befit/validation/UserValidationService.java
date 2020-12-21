package pl.umk.mat.git2befit.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.User;

@Service
public class UserValidationService {

    private final String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private final String passwordRegex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

    public UserValidationService() {}

    public boolean validateEmail(String email) {
        return email.matches(this.emailRegex);
    }

    private boolean validateUserId(long id) {
        return id > 0;
    }

    private boolean validatePassword(String password) {
        return password.matches(this.passwordRegex);
    }

    public boolean validateUser(User user) {
        return  validateUserId(user.getId()) &&
                validateEmail(user.getEmail()) &&
                validatePassword(user.getPassword());
    }
}
