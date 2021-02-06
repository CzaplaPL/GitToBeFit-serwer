package pl.umk.mat.git2befit.model.training.generation.model;

import org.springframework.web.bind.annotation.GetMapping;
import pl.umk.mat.git2befit.model.entity.workout.Exercise;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Entity
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 51L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    /*@JoinTable(name = "exersises_in_plan",
            joinColumns = {@JoinColumn(name="exersise_id",referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="training_plan_id",referencedColumnName="id")}
    )*/
    //private List<Exercise> exerciseList;
    private Map<Exercise, ExerciseFormat> format;


    public TrainingPlan() {
    }
}
