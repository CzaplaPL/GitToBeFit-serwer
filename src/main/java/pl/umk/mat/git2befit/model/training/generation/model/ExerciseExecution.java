package pl.umk.mat.git2befit.model.training.generation.model;

import pl.umk.mat.git2befit.model.entity.workout.Exercise;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ExerciseExecution implements Serializable {
    private static final long serialVersionUID = 984652L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int time;
    private int series;
    private int count;
    @ManyToOne
    private Exercise exercise;

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "ExerciseExecution{" +
                "id=" + id +
                ", time=" + time +
                ", series=" + series +
                ", count=" + count +
                ", exercise=" + exercise +
                '}';
    }
}
