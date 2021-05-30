package pl.umk.mat.git2befit.user.validation;

import pl.umk.mat.git2befit.user.exception.EmailValidationException;
import pl.umk.mat.git2befit.user.exception.WeakPasswordException;
import pl.umk.mat.git2befit.user.model.entity.User;

import java.util.regex.Pattern;

public class UserValidationService {

    private final static Pattern EMAIL = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    public final static Pattern PASSWORD  = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!`'@#$%^&()\\\\{}\\[\\]:;<>,?/~_+\\-=|])(?=\\S+$).{8,}");
    //8 znaków, mała i duża litera, cyfra, znak specjalny

    private UserValidationService() {}

    public static void validateEmail(String email) throws EmailValidationException {
        if(!EMAIL.matcher(email).matches())
            throw new EmailValidationException("bad email");
    }

    public static void validatePassword(String password) throws WeakPasswordException {
        if(!PASSWORD.matcher(password).matches())
            throw new WeakPasswordException("weak password");
    }

    public static void validateUser(User user) throws EmailValidationException, WeakPasswordException {
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }
}
