package pl.umk.mat.git2befit.validation.workout;

import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.Training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FBWValidator {
    private List<String> bodyPartsList = Arrays.asList("SIXPACK", "CALVES", "BICEPS", "TRICEPS", "SHOULDERS", "CHEST", "BACK", "THIGHS");

    public void validateTraining(List<Training> trainingList) {
        checkIfAllBodyPartsHaveExercise(trainingList);
    }

    private void checkIfAllBodyPartsHaveExercise(List<Training> trainingList) {
        ArrayList<String> bodyParts = new ArrayList<>();
        for (Training trainingDay : trainingList) {
            trainingDay.getExercisesExecutions()
                    .forEach(exerciseExecution -> bodyParts.add(exerciseExecution.getExercise().getBodyPart().getName()));
            bodyPartsList.removeAll(bodyParts);
            if (bodyPartsList.size() != 0){
                throw new NotValidTrainingException("not enough exercises for %s".formatted(bodyPartsList));
            }
        }
    }
}
