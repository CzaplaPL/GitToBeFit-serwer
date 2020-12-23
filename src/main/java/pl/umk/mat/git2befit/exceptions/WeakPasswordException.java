package pl.umk.mat.git2befit.exceptions;

public class WeakPasswordException extends Exception{
    public WeakPasswordException(String errorMessage){
        super(errorMessage);
    }
}
