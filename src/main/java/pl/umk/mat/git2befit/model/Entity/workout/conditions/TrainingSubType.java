package pl.umk.mat.git2befit.model.Entity.workout.conditions;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "training_sub_types")
public class TrainingSubType implements Serializable {
    private static final long serialVersionUID = 7L;

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
        return "TrainingSubType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
