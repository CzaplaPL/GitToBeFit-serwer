package pl.umk.mat.git2befit.service.workout.factory;

import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.Exercise;
import pl.umk.mat.git2befit.model.workout.equipment.Equipment;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;
import java.util.stream.Collectors;

public interface TrainingPlanInterface {
    long DEFAULT_BREAK_TIME = 30;
    long DEFAULT_CIRCUIT_COUNT = 3;
    int DEFAULT_SERIES_COUNT = 3;
    int DEFAULT_COUNT_OF_REPEATITIONS = 8;
    int DEFAULT_EXERCISE_TIME_EXECUTION = 3;
    int NOT_APPLICABLE = 0;

    List<Training> create(TrainingForm trainingForm);

    void validateAfterCreating() throws NotValidTrainingException;

    default List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises, List<Long> availableEquipments){
        return exercises.stream().filter(exercise -> {
            for(Equipment equipment: exercise.getEquipmentsNeeded()) {
                if (!availableEquipments.contains(equipment.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    default ExerciseExecution getExactExerciseExecution(Exercise exercise, TrainingForm trainingForm) {
        ExerciseExecution exerciseExecution = new ExerciseExecution();
        String scheduleType = exercise.getScheduleType().getName();
        exerciseExecution.setExercise(exercise);
        exerciseExecution.setSeries(trainingForm.checkIfScheduleTypeIsCircuit() ? NOT_APPLICABLE : DEFAULT_SERIES_COUNT);
        if (scheduleType.equalsIgnoreCase("REPEAT")) {
            exerciseExecution.setCount(DEFAULT_COUNT_OF_REPEATITIONS);
            exerciseExecution.setTime(NOT_APPLICABLE);
        } else if (scheduleType.equalsIgnoreCase("TIME")) {
            exerciseExecution.setCount(NOT_APPLICABLE);
            exerciseExecution.setTime(DEFAULT_EXERCISE_TIME_EXECUTION);
        }
        return exerciseExecution;
    }
}
