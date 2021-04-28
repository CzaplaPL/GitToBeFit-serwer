package pl.umk.mat.git2befit.model.workout.training;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Training implements Serializable {
    private static final long serialVersionUID = 51L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long breakTime;
    private long circuitsCount;

    @ElementCollection
    @CollectionTable(name = "exercises_executions")
    private List<ExerciseExecution> exercisesExecutions = new ArrayList<>();
    private int dayOfTraining;

    public Training() {}

    public Training(
            long breakTime,
            long circuitsCount,
            List<ExerciseExecution> exercisesExecutions
    ) {
        this.breakTime = breakTime;
        this.circuitsCount = circuitsCount;
        this.exercisesExecutions = exercisesExecutions;
        dayOfTraining = 0;
    }

    public void addExerciseExecution(List<ExerciseExecution> exerciseExecutions){
        this.exercisesExecutions = new ArrayList<>(getExercisesExecutions());
        this.exercisesExecutions.addAll(exerciseExecutions);
    }
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

    public long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public long getCircuitsCount() {
        return circuitsCount;
    }

    public void setCircuitsCount(long circuitsCount) {

        this.circuitsCount = circuitsCount;
    }

    public int getDayOfTraining() {
        return dayOfTraining;
    }

    public void setDayOfTraining(int dayOfTraining) {
        this.dayOfTraining = dayOfTraining;
    }
}
