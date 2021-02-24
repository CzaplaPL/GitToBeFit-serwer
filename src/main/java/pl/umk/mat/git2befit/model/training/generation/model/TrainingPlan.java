package pl.umk.mat.git2befit.model.training.generation.model;

import pl.umk.mat.git2befit.model.entity.workout.Exercise;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 51L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long trainingId;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ExerciseExecution> exercisesExecutions;

    public List<ExerciseExecution> getExercisesExecutions() {
        return exercisesExecutions;
    }

    public void setExercisesExecutions(List<ExerciseExecution> exercisesExecutions) {
        this.exercisesExecutions = exercisesExecutions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(long trainingId) {
        this.trainingId = trainingId;
    }
/*
    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
*/
}
