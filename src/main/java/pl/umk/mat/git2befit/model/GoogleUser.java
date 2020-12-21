package pl.umk.mat.git2befit.model;


public class GoogleUser {
    private String idToken;

    public GoogleUser(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
