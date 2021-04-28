package pl.umk.mat.git2befit.user.model.entity;


import javax.persistence.*;
import java.io.Serializable;


@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean enable;

    public User(){
        this.enable = false;
    }

    public User(String nick, String password) {
        super();
        this.email = nick;
        this.password = password;
    }

    public User(String email, String password, boolean enable) {
        this.email = email;
        this.password = password;
        this.enable = enable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String nick) {
        this.email = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nick='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
