package pl.umk.mat.git2befit.training.service.factory;

import pl.umk.mat.git2befit.training.model.equipment.Equipment;
import pl.umk.mat.git2befit.training.model.training.Exercise;
import pl.umk.mat.git2befit.training.model.training.ExerciseExecution;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;

import java.util.List;
import java.util.stream.Collectors;

public interface TrainingPlanGenerator {
    long DEFAULT_BREAK_TIME = 30;
    long DEFAULT_CIRCUIT_COUNT = 3;
    int DEFAULT_SERIES_COUNT = 3;
    int DEFAULT_COUNT_OF_REPEATITIONS = 8;
    int DEFAULT_EXERCISE_TIME_EXECUTION = 30;
    int NOT_APPLICABLE = 0;

    TrainingPlan create(TrainingForm trainingForm);

    void validate(TrainingPlan trainingPlan, TrainingForm trainingForm);

    default List<Exercise> filterAllByAvailableEquipment(List<Exercise> exercises, List<Long> availableEquipments) {
        return exercises.stream().filter(exercise -> {
            for (Equipment equipment : exercise.getEquipmentsNeeded()) {
                if (!availableEquipments.contains(equipment.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    default ExerciseExecution getExactExerciseExecution(Exercise exercise, TrainingForm trainingForm) {
        String scheduleType = exercise.getScheduleType().getName();
        return new ExerciseExecution(
                exercise,
                scheduleType.equalsIgnoreCase("REPEAT") ? NOT_APPLICABLE : DEFAULT_EXERCISE_TIME_EXECUTION,
                trainingForm.checkIfScheduleTypeIsCircuit() ? NOT_APPLICABLE : DEFAULT_SERIES_COUNT,
                scheduleType.equalsIgnoreCase("REPEAT") ? DEFAULT_COUNT_OF_REPEATITIONS : NOT_APPLICABLE
        );
    }
}
