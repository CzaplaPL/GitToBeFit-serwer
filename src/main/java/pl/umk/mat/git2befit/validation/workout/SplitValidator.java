package pl.umk.mat.git2befit.validation.workout;

import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.Training;

import java.util.List;

@Component
public class SplitValidator {

    public static void validateTraining(List<Training> trainingList) {
        buu();
    }

    private static void buu() {
        throw new NotValidTrainingException("BUUUU");
    }
}