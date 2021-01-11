package pl.umk.mat.git2befit.validation;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.exceptions.WeakPasswordException;
import pl.umk.mat.git2befit.model.entity.User;

@Service
public class UserValidationService {

    private final String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public final static String PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!#$%^&+=])(?=\\S+$).{8,}";
    //8 znaków, mała i duża litera, cyfra, znak specjalny

    public UserValidationService() {}

    public void validateEmail(String email) throws EmailException {
        if(!email.matches(emailRegex))
            throw new EmailException("bad email");
    }

    public void validatePassword(String password) throws WeakPasswordException {
        if(!password.matches(PASSWORD_REGEX))
            throw new WeakPasswordException("weak password");
    }

    public void validateUser(User user) throws EmailException, WeakPasswordException {
            validateEmail(user.getEmail());
            validatePassword(user.getPassword());
    }
}
