package pl.umk.mat.git2befit.user.exception;

public class WeakPasswordException extends Exception{
    public WeakPasswordException(String errorMessage){
        super(errorMessage);
    }
}
