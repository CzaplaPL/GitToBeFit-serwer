package pl.umk.mat.git2befit.model.workout.conditions;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "training_types")
public class TrainingType implements Serializable {
    private static final long serialVersionUID = 8L;

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

    public TrainingType(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TrainingType(){}

    @Override
    public String toString() {
        return "TrainingType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

