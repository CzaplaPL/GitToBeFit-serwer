package pl.umk.mat.git2befit.validation.workout.implementation;

import pl.umk.mat.git2befit.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.model.workout.training.Training;
import pl.umk.mat.git2befit.validation.workout.TrainingValidator;

import java.util.List;

public class SplitValidator implements TrainingValidator {

    @Override
    public void validateTraining(List<Training> trainingList) {
        buu();
    }

    private void buu() {
        throw new NotValidTrainingException("BUUUU");
    }
}