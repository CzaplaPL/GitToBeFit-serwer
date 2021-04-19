package pl.umk.mat.git2befit.model.workout.training;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ExerciseExecution implements Serializable {
    private static final long serialVersionUID = 984652L;

    @ManyToOne
    private Exercise exercise;
    private int time;
    private int series;
    private int count;

    public ExerciseExecution() {}

    public ExerciseExecution(Exercise exercise, int time, int series, int count) {
        this.exercise = exercise;
        this.time = time;
        this.series = series;
        this.count = count;
    }

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
                "exercise=" + exercise +
                ", time=" + time +
                ", series=" + series +
                ", count=" + count +
                '}';
    }
}
