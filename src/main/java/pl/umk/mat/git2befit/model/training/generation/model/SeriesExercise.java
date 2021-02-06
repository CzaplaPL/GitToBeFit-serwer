package pl.umk.mat.git2befit.model.training.generation.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class SeriesExercise extends ExerciseFormat implements Serializable {
    private static final long serialVersionUID = 52L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int series;
    private int repeat;

}