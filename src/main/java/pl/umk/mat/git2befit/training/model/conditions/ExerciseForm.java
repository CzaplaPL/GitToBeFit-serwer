package pl.umk.mat.git2befit.training.model.conditions;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "exercise_forms")
public class ExerciseForm implements Serializable {
    private static final long serialVersionUID = 6L;

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

    public ExerciseForm(){}

    public ExerciseForm(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
