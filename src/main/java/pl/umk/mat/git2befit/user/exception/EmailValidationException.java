package pl.umk.mat.git2befit.user.exception;

public class EmailValidationException extends Exception {
    public EmailValidationException(String errorMessage){
        super(errorMessage);
    }
}
