package pl.umk.mat.git2befit.training.validation;

import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.training.ExerciseExecution;
import pl.umk.mat.git2befit.training.model.training.Training;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitValidator {
    private final List<String> smallBodyParts = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS");
    private final List<String> bigBodyParts = List.of("CHEST", "BACK", "THIGHS");
    private final int amountForSmall = 3;
    private final int amountForBig = 4;

    public void validateTraining(TrainingPlan trainingPlan, TrainingForm trainingForm) {
        validateDaysCount(trainingForm);
        validateAmountOfExercises(prepare(trainingPlan), trainingForm);
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

    private void validateAmountOfExercises(List<Map<String, List<ExerciseExecution>>> trainingList, TrainingForm trainingForm) {
        Map<String, List<ExerciseExecution>> map = new HashMap<>();
        trainingList.forEach(map::putAll);
        List<String> errors = new ArrayList<>();

        for (String bodyPart : trainingForm.getBodyParts()){
            if(map.containsKey(bodyPart)) {
                if (map.get(bodyPart).size() != getAmountOfExercisesForBodyPart(bodyPart)) {
                    errors.add(bodyPart);
                }
            }else {
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