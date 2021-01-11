package pl.umk.mat.git2befit.model.Entity.workout.equipment;

import pl.umk.mat.git2befit.model.Entity.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "equipment")
public class Equipment implements Serializable {
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private EquipmentType type;
    @Column(nullable = false, unique = true)
    private String name;
    @Column
    private String url;

    public Equipment() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", user=" + user +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
