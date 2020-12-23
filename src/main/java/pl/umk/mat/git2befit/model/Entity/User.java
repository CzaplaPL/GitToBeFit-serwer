package pl.umk.mat.git2befit.model.Entity;

import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

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

    public User(){}

    public User(String nick, String password) {
        this.email = nick;
        this.password = password;
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

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", nick='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
