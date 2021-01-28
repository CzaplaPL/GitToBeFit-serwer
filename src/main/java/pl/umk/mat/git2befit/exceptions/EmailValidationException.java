package pl.umk.mat.git2befit.exceptions;

public class EmailValidationException extends Exception {
    public EmailValidationException(String errorMessage){
        super(errorMessage);
    }
}
