package pl.umk.mat.git2befit.validation;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.exceptions.EmailValidationException;
import pl.umk.mat.git2befit.exceptions.WeakPasswordException;
import pl.umk.mat.git2befit.model.entity.User;

@Service
public class UserValidationService {

    private final static String emailRegex = "^[\\w!#$%&'+/=?`{|}~^-]+(?:\\.[\\w!#$%&'+/=?`{|}~^-]+)@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public final static String PASSWORD_REGEX = "(?=.[0-9])(?=.[a-z])(?=.[A-Z])(?=.*[@!#$%^&+=])(?=\\S+$).{8,}";
    //8 znaków, mała i duża litera, cyfra, znak specjalny

    private UserValidationService() {}

    public static void validateEmail(String email) throws EmailValidationException {
        if(!email.matches(emailRegex))
            throw new EmailValidationException("bad email");
    }

    public static void validatePassword(String password) throws WeakPasswordException {
        if(!password.matches(PASSWORD_REGEX))
            throw new WeakPasswordException("weak password");
    }

    public static void validateUser(User user) throws EmailValidationException, WeakPasswordException {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }
}
