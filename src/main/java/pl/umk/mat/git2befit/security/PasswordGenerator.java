package pl.umk.mat.git2befit.security;

import com.github.curiousoddman.rgxgen.RgxGen;


import static pl.umk.mat.git2befit.validation.UserValidationService.PASSWORD;

public class PasswordGenerator {
    public static String generateRandomPassword(){
        RgxGen rgxGen = new RgxGen(PASSWORD.pattern());
        return rgxGen.generate().replaceAll(" ", "");
    }

}
