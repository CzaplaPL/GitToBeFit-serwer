package pl.umk.mat.git2befit.model.training.generation.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class TimeExercise extends ExerciseFormat implements Serializable {
    private static final long serialVersionUID = 50L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int time;

}
