package pl.umk.mat.git2befit.validation.workout;


import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.ExerciseExecution;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;
import pl.umk.mat.git2befit.model.workout.training.TrainingPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class SplitValidator {
    private final List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS");
    private final List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
    private final int amountForSmall = 3;
    private final int amountForBig = 4;

    public void validateTraining(TrainingPlan trainingPlan, TrainingForm trainingForm) {
        validateDaysCount(trainingForm);
        validateAmountOfExercises(prepare(trainingPlan));
    }

    private List<Map<String, List<ExerciseExecution>>> prepare(TrainingPlan plan) {
        List<Map<String, List<ExerciseExecution>>> listToReturn = new ArrayList<>();
        for (Training training : plan.getPlanList()) {
            Map<String, List<ExerciseExecution>> map = new HashMap<>();
            training.getExercisesExecutions()
                    .stream()
                    .map(exerciseExecution -> exerciseExecution.getExercise().getBodyPart().getName())
                    .distinct()
                    .forEach(s -> map.put(s, new ArrayList<>()));
            training.getExercisesExecutions()
                    .forEach(exerciseExecution -> {
                        String name = exerciseExecution.getExercise().getBodyPart().getName();
                        map.get(name).add(exerciseExecution);
                    });
            listToReturn.add(map);
        }
        return listToReturn;
    }

    private void validateAmountOfExercises(List<Map<String, List<ExerciseExecution>>> trainingList) {
        Map<String, List<ExerciseExecution>> map = new HashMap<>();
        trainingList.forEach(map::putAll);
        List<String> errors = new ArrayList<>();

        for (String bodyPart : map.keySet()){
            if(map.get(bodyPart).size() != getAmountOfExercisesForBodyPart(bodyPart)){
                errors.add(bodyPart);
            }
        }
        if (!errors.isEmpty()){
            throw new NotValidTrainingException("not enough exercises for: %s".formatted(errors.toString()));
        }
    }

    private int getAmountOfExercisesForBodyPart(String bodyPart){
        if(smallBodyParts.contains(bodyPart)){
            return amountForSmall;
        }else
            return amountForBig;
    }

    private void validateDaysCount(TrainingForm trainingForm){
        if(trainingForm.getDaysCount() > trainingForm.getBodyParts().size()) {
            throw new NotValidTrainingException("not enough days for set body parts");
        }
    }
}