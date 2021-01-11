package pl.umk.mat.git2befit.model.entity.workout.conditions;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "body_parts")
public class BodyPart implements Serializable {
    private static final long serialVersionUID = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String name;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BodyPart{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
