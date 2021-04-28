package pl.umk.mat.git2befit.config.security.service;

import com.github.curiousoddman.rgxgen.RgxGen;
import pl.umk.mat.git2befit.user.exception.WeakPasswordException;
import pl.umk.mat.git2befit.user.validation.UserValidationService;


import static pl.umk.mat.git2befit.user.validation.UserValidationService.PASSWORD;

public class PasswordGenerator {
    private static RgxGen rgxGen = new RgxGen(PASSWORD.pattern());

    public static String generateRandomPassword() {
        boolean isNewPasswordValidate = false;
        String substring = "";
        while (!isNewPasswordValidate) {
            try {
                substring = getNewRandomPassword();
                UserValidationService.validatePassword(substring);
                isNewPasswordValidate = true;
            } catch (WeakPasswordException ignore) {}
        }
        return substring;
    }

    private static String getNewRandomPassword() {
        String newPassword = rgxGen.generate().replaceAll(" ", "");
        return newPassword.length() > 10 ? newPassword.substring(0, 11) : newPassword;
    }
}
